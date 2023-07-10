package com.jaehl.spaceTraders.ui.pages.runTask.tasks

import com.jaehl.spaceTraders.data.controllers.ShipControllerImp
import com.jaehl.spaceTraders.data.model.MiningSurvey
import com.jaehl.spaceTraders.data.model.Ship
import com.jaehl.spaceTraders.data.services.ContractService
import com.jaehl.spaceTraders.data.services.FleetService
import com.jaehl.spaceTraders.ui.pages.shipDetails.tasks.OreMineRefineSellTask
import com.jaehl.spaceTraders.ui.pages.shipDetails.tasks.SellOrder
import com.jaehl.spaceTraders.util.DateHelper
import com.jaehl.spaceTraders.util.Logger
import java.util.*

class MiningTask (
    private val logger : Logger,
    private val fleetService : FleetService,
    private val contractService : ContractService,
    private val dateHelper: DateHelper
) {

    private var shipStatesList : List<ShipStateController> = listOf()

    lateinit var sellOrders: List<SellOrder>
    lateinit var localSell : List<String>
    lateinit var localKeep : List<String>

    lateinit var asteroidField : String

    private var miningSurvey : MiningSurvey = MiningSurvey(
        expiration = Date(0)
    )

    val refineList : List<OreMineRefineSellTask.RefineItem> = listOf(
        OreMineRefineSellTask.RefineItem(
            baseItemId = "PLATINUM_ORE",
            refineItemId = "PLATINUM"
        ),
        OreMineRefineSellTask.RefineItem(
            baseItemId = "GOLD_ORE",
            refineItemId = "GOLD"
        )
    )

    private fun getCargoShip() : ShipStateController? {
        return shipStatesList.firstOrNull { shipStateController ->
            shipStateController.canTransport
                    && shipStateController.ship().nav.status == Ship.Nav.Statue.InOrbit
                    && shipStateController.ship().nav.route.destination.symbol == asteroidField
        }
    }

    private fun getRefineShip() : ShipStateController? {
        return shipStatesList.firstOrNull { shipStateController ->
            shipStateController.canRefine
                    && shipStateController.ship().nav.status == Ship.Nav.Statue.Docked
                    && shipStateController.ship().nav.route.destination.symbol == asteroidField
        }
    }

    private fun getSmallestCoolDownDelay() : Long{
        var delayTime = Long.MAX_VALUE
        shipStatesList.forEach {

            if(!it.getCoolDown().isFinished() && it.getCoolDown().getDelay() < delayTime){
                delayTime = it.getCoolDown().getDelay()
            }
        }
        if(delayTime == Long.MAX_VALUE){
            delayTime = 0
        }
        return delayTime
    }

    private fun getSellOrder(destinationId : String) : SellOrder {
        return sellOrders.first {it.destinationId == destinationId}
    }

    private fun getNextSellOrder(currentDestinationId : String) : SellOrder?{
        val index = sellOrders.indexOf(sellOrders.first { it.destinationId == currentDestinationId })
        if((index + 1) >= sellOrders.size) return null
        return sellOrders[index + 1]
    }
    
    suspend fun start(){
        shipStatesList = listOf(
            ShipStateController(
                ship = fleetService.getShip(""),
                shipState = ShipState.Wait
            )
        )
        shipStatesList.forEach { 
            it.setup()
        }
    }
    
    private inner class ShipStateController(
        ship : Ship,
        private var shipState : ShipState = ShipState.Wait,
        private var useSurvey : Boolean = false,
        val canSurvey : Boolean = false,
        val canTransport : Boolean = false,
        val canRefine : Boolean = false,
        val canMine : Boolean = true,
    ) {
        private val shipController = ShipControllerImp(
            ship = ship,
            fleetService = fleetService,
            contractService = contractService,
            dateHelper = dateHelper
        )
        
        fun getCoolDown() = shipController.getCoolDown()
        
        fun ship() = shipController.getShip()
        
        fun setup() {
            logger.log("${shipController.getShipId()} setup")
            when(shipState) {
                is ShipState.Mining -> {
                    shipController.enterOrbit()
                }
                is ShipState.Surveying -> {
                    shipController.enterOrbit()
                }
                is ShipState.Refine -> {
                    shipController.dock()
                }
                is ShipState.CargoWait -> {
                    shipController.dock()
                }
                else -> {}
            }
        }

        fun run(){
            when(shipState){
                is ShipState.Mining -> {
                    if (!shipController.getCoolDown().isFinished()) return
                    if (useSurvey && miningSurvey.isExpired()) return
                    if(ship().cargo.isFull()){
                        shipController.dock()
                        shipController.sellAll(localSell)
                        shipController.enterOrbit()
                        shipController.jettisonAllBut(localKeep)
                    }


                    if (canTransport) return
                    val cargoShip = getCargoShip() ?: return
                    val refineShip = getRefineShip()

                    refineShip?.let {
                        shipController.transferAllCargo(refineShip.shipController, refineList.map { it.baseItemId })
                    }

                    shipController.transferAllCargo(cargoShip.shipController, localKeep)

                    if(!canMine) return
                    
                    shipController.extract(if(useSurvey) miningSurvey else null)
                }
                is ShipState.Surveying -> {
                    if (!getCoolDown().isFinished()) return
                    val responseSurvey = shipController.survey(refineList.map { it.baseItemId })
                    if(responseSurvey != null) {
                        miningSurvey = responseSurvey
                    }
                }
                is ShipState.Refine -> {
                    val cargoShip = getCargoShip()
                    
                    cargoShip?.let{
                        shipController.transferAllCargo(it.shipController, refineList.map { it.refineItemId })
                    }
                    if (!getCoolDown().isFinished()) return
                    
                    val cargo = ship().cargo.inventory.firstOrNull { cargoItem -> refineList.map { it.baseItemId }.contains(cargoItem.symbol) && cargoItem.units > 3 }
                    if (cargo == null) {
                        //ADD 30 sec coolDown waiting for a mining task to deliver cargo
                        //TODO buy raw mats for process if none from mining
                        shipController.setCoolDown(500)
                    } else {
                        shipController.refine(refineList.first { it.baseItemId == cargo.symbol }.refineItemId)
                    }
                }
                else -> {}
            }
        }

        fun calculateState() {
            when(val currentState = shipState){
                is ShipState.Mining -> {
                    if(miningSurvey.isExpired() && canSurvey && useSurvey){
                        shipController.enterOrbit()
                        shipState = ShipState.Surveying
                        return
                    }
                    if(ship().cargo.isFull() && canTransport){
                        shipController.enterOrbit()
                        shipController.navigate(sellOrders.first().destinationId)
                        shipState = ShipState.TransportSell(sellOrders.first().destinationId)
                    }
                }
                is ShipState.Surveying -> {
                    if (miningSurvey.isExpired()) return
                    shipController.dock()
                    shipState = ShipState.Mining
                }
                is ShipState.TransportSell -> {
                    if(!getCoolDown().isFinished()) return
                    shipController.dock()
                    shipController.sellAll(getSellOrder(currentState.destination).items)

                    if(ship().fuel.current < 100){
                        shipController.refuel()
                    }
                    shipController.enterOrbit()
                    val nextSellOrder = getNextSellOrder(currentState.destination)
                    if(nextSellOrder == null){
                        shipState = ShipState.NavigateBackToAsteroidField
                        shipController.navigate(asteroidField)
                    } else {
                        shipState = ShipState.TransportSell(nextSellOrder.destinationId)
                        shipController.navigate(nextSellOrder.destinationId)
                    }
                }
                is ShipState.NavigateBackToAsteroidField -> {
                    if(!getCoolDown().isFinished()) return
                    if(canMine){
                        shipState = ShipState.Mining
                    }
                    else if (canTransport) {
                        shipState = ShipState.CargoWait
                    }
                }
                is ShipState.Refine -> {}
                is ShipState.CargoWait -> {
                    if(ship().cargo.isFull() && canTransport){
                        shipController.enterOrbit()
                        shipController.navigate(sellOrders.first().destinationId)
                        shipState = ShipState.TransportSell(sellOrders.first().destinationId)
                    }
                }
                else -> {
                    logger.error("calculateState : error ${ship().symbol}")
                }
            }
        }
    }

    private sealed class ShipState {
        object Wait : ShipState()
        object Mining : ShipState()
        object Surveying : ShipState()
        data class TransportSell(val destination : String) : ShipState()
        object NavigateBackToAsteroidField : ShipState()
        object Refine : ShipState()
        object CargoWait : ShipState()
    }
}