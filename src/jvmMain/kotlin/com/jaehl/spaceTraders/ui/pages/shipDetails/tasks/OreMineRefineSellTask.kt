package com.jaehl.spaceTraders.ui.pages.shipDetails.tasks

import com.jaehl.spaceTraders.data.model.MiningSurvey
import com.jaehl.spaceTraders.data.model.Ship
import com.jaehl.spaceTraders.data.model.response.Cooldown
import com.jaehl.spaceTraders.data.services.FleetService
import com.jaehl.spaceTraders.util.Logger
import kotlinx.coroutines.delay
import java.util.*
import javax.inject.Inject

class  OreMineRefineSellTask @Inject constructor(
    private val logger : Logger,
    private val fleetService : FleetService,
    private val basicTasks : BasicTasks
) {

    private var shipStatesList : List<ShipState> = listOf()
    private var miningSurvey : MiningSurvey = MiningSurvey(
        expiration = Date(0)
    )
    lateinit var sellOrders: List<SellOrder>
    lateinit var localSell : List<String>
    lateinit var localKeep : List<String>

    lateinit var asteroidField : String

    val refineList : List<RefineItem> = listOf(
        RefineItem(
            baseItemId = "PLATINUM_ORE",
            refineItemId = "PLATINUM"
        ),
        RefineItem(
            baseItemId = "GOLD_ORE",
            refineItemId = "GOLD"
        )
    )

    suspend fun start(asteroid : SellOrder, sellOrders : List<SellOrder>) {
        logger.log("OreMineRefineSellTask start")
        this.asteroidField = asteroid.destinationId
        this.sellOrders = sellOrders

        val sellItemArray = arrayListOf<String>()
        sellOrders.forEach {sellOder ->
            sellOder.items.forEach {
                sellItemArray.add(it)
            }
        }
        sellItemArray.toSet().toList()
        localKeep = sellItemArray

        var localSell = arrayListOf<String>()
        asteroid.items.forEach {
            if(!sellItemArray.contains(it)){
                localSell.add(it)
            }
        }
        this.localSell = localSell

        shipStatesList = listOf(
            ShipState(
                ship = basicTasks.getShip("TANGO42-1"),
                canSurvey = true,
                canTransport = true,
                canMine = false,
                state = State.Surveying
            ),
            ShipState(
                ship = basicTasks.getShip("TANGO42-2")
            ),
            ShipState(
                ship = basicTasks.getShip("TANGO42-3")
            ),
            ShipState(
                ship = basicTasks.getShip("TANGO42-4")
            ),
            ShipState(
                ship = basicTasks.getShip("TANGO42-5"),
                canSurvey = true,
                state = State.Surveying
            ),
            ShipState(
                ship = basicTasks.getShip("TANGO42-6"),
                canSurvey = true,
                state = State.Surveying
            ),
            ShipState(
                ship = basicTasks.getShip("TANGO42-7"),
                state = State.Refine,
                canMine = false,
                canRefine = true

            ),
            ShipState(
                ship = basicTasks.getShip("TANGO42-8"),
                canSurvey = true,
                state = State.Surveying
            )
        )

        shipStatesList.forEach {
            it.setup()
        }

        while (true) {
            shipStatesList.forEach {
                it.calculateState()
                it.run()
                it.calculateState()
            }
            var delayTime = getSmallestCoolDownDelay()
            logger.log("delayTime : ${(delayTime/1000).toInt()}")
            delay(delayTime)
        }
    }

    private fun getSmallestCoolDownDelay() : Long{
        var delayTime = Long.MAX_VALUE
        shipStatesList.forEach {
            if(it.shipCoolDown) return@forEach
            if(!it.coolDown.isFinished() && it.coolDown.getDelay() < delayTime){
                delayTime = it.coolDown.getDelay()
            }
        }
        if(delayTime == Long.MAX_VALUE){
            delayTime = 0
        }
        return delayTime
    }

    private fun getCargoShip() : ShipState? {
        return shipStatesList.firstOrNull { shipState ->
            shipState.canTransport
                    && shipState.ship.nav.status == Ship.Nav.Statue.Docked
                    && shipState.ship.nav.route.destination.symbol == asteroidField
        }
    }
    private fun getRefineShip() : ShipState? {
        return shipStatesList.firstOrNull { shipState ->
            shipState.canRefine
                    && shipState.ship.nav.status == Ship.Nav.Statue.Docked
                    && shipState.ship.nav.route.destination.symbol == asteroidField
        }
    }

    private fun getSellOrder(destinationId : String) : SellOrder {
        return sellOrders.first {it.destinationId == destinationId}
    }

    private fun getNextSellOrder(currentDestinationId : String) : SellOrder?{
        val index = sellOrders.indexOf(sellOrders.first { it.destinationId == currentDestinationId })
        if((index + 1) >= sellOrders.size) return null
        return sellOrders[index + 1]
    }

    private inner class ShipState(
        var ship : Ship,
        val canSurvey : Boolean = false,
        val canTransport : Boolean = false,
        val canRefine : Boolean = false,
        val canMine : Boolean = true,
        var coolDown: Cooldown = Cooldown(),
        var shipCoolDown : Boolean = false,
        var state : State = State.Mining
    ) {
        suspend fun setup() {
            logger.log("${ship.symbol} setup")
            when(state) {
                is State.Mining -> {
                    if(ship.nav.status == Ship.Nav.Statue.InOrbit) {
                        ship = basicTasks.dock(ship)
                    }
                    coolDown = basicTasks.getShipCoolDown(shipId = ship.symbol) ?: Cooldown()
                }
                is State.Surveying -> {
                    if(ship.nav.status == Ship.Nav.Statue.Docked) {
                        ship = basicTasks.enterOrbit(ship)
                    }
                    coolDown = basicTasks.getShipCoolDown(shipId = ship.symbol) ?: Cooldown()
                }
                is State.Refine -> {
                    if(ship.nav.status == Ship.Nav.Statue.InOrbit) {
                        ship = basicTasks.dock(ship)
                    }
                    coolDown = basicTasks.getShipCoolDown(shipId = ship.symbol) ?: Cooldown()
                }
                else -> {}
            }
        }

        suspend fun run(){
            when(state){
                is State.Mining -> {
                    if (!coolDown.isFinished()) return
                    if (miningSurvey.isExpired()) return
                    if(ship.cargo.isFull()){
                        ship = basicTasks.sell(ship, localSell)
                        ship = basicTasks.enterOrbit(ship)
                        ship = basicTasks.jettisonTask(ship, localKeep)
                        ship = basicTasks.dock(ship)
                    }
                    if(canMine == false) return
                    val response = basicTasks.mineWithoutDelay(ship, miningSurvey)
                    ship = response.data
                    coolDown = response.cooldown

                    if (canTransport) return
                    val cargoShip = getCargoShip() ?: return
                    val refineShip = getRefineShip()

                    refineShip?.let {
                        refineList.forEach {
                            val response = basicTasks.transferTask(ship, refineShip.ship, it.baseItemId)
                            ship = response.first
                            refineShip.ship = response.second
                        }
                    }

                    localKeep.forEach {
                        val response = basicTasks.transferTask(ship, cargoShip.ship, it)
                        ship = response.first
                        cargoShip.ship = response.second
                    }
                }
                is State.Surveying -> {
                    if (!coolDown.isFinished()) return
                    val reponse = basicTasks.surveyWithoutDelay(ship, refineList.map { it.baseItemId })
                    coolDown = reponse.cooldown
                    if(reponse.data != null){
                        miningSurvey = reponse.data
                    }
                }
                is State.Refine -> {
                    val cargoShip = getCargoShip()
                    cargoShip?.let{
                        refineList.forEach {
                            val response = basicTasks.transferTask(ship, cargoShip.ship, it.refineItemId)
                            ship = response.first
                            cargoShip.ship = response.second
                        }
                    }
                    if (!coolDown.isFinished()) return
                    val cargo = ship.cargo.inventory.firstOrNull { cargoItem -> refineList.map { it.baseItemId }.contains(cargoItem.symbol) && cargoItem.units > 3 }
                    if(cargo == null) {
                        shipCoolDown = true //TODO Add buy action
                    } else {
                        shipCoolDown = false
                        val response = basicTasks.refine(ship, refineList.first{it.baseItemId == cargo.symbol}.refineItemId)
                        ship = response.first
                        coolDown = response.second
                    }
                }
                else -> {}
            }
        }
        suspend fun calculateState() {
            when(val currentState = state){
                is State.Mining -> {
                    if(miningSurvey.isExpired() && canSurvey){
                        ship = basicTasks.enterOrbit(ship)
                        state = State.Surveying
                        return
                    }
                    if(ship.cargo.isFull() && canTransport){
                        ship = basicTasks.enterOrbit(ship)
                        ship = basicTasks.navigateWithoutDelay(ship, sellOrders.first().destinationId)
                        coolDown = Cooldown(
                            expiration = ship.nav.route.arrival
                        )
                        state = State.TransportSell(sellOrders.first().destinationId)
                    }
                }
                is State.Surveying -> {
                    if (miningSurvey.isExpired()) return
                    ship = basicTasks.dock(ship)
                    state = State.Mining
                }
                is State.TransportSell -> {
                    if(!coolDown.isFinished()) return
                    ship = basicTasks.dock(ship)
                    ship = basicTasks.sell(ship, getSellOrder(currentState.destination).items)
                    if(ship.fuel.current < 100){
                        ship = basicTasks.shipRefuelTask(ship)
                    }
                    ship = basicTasks.enterOrbit(ship)
                    val nextSellOrder = getNextSellOrder(currentState.destination)
                    if(nextSellOrder == null){
                        state = State.NavigateBackToAsteroidField
                        ship = basicTasks.navigateWithoutDelay(ship, asteroidField)
                        coolDown = Cooldown(
                            expiration = ship.nav.route.arrival
                        )
                    } else {
                        state = State.TransportSell(nextSellOrder.destinationId)
                        ship = basicTasks.navigateWithoutDelay(ship, nextSellOrder.destinationId)
                        coolDown = Cooldown(
                            expiration = ship.nav.route.arrival
                        )
                    }
                }
                is State.NavigateBackToAsteroidField -> {
                    if(!coolDown.isFinished()) return
                    ship = basicTasks.dock(ship)
                    state = State.Mining
                }
                is State.Refine -> {}
                else -> {
                    logger.error("calculateState : error ${ship.symbol}")
                }
            }
        }
    }

    data class RefineItem(
        val baseItemId : String,
        val refineItemId : String,
        val baseAmountNeeded : Int = 3
    )

    private sealed class State {
        object Mining : State()
        object Surveying : State()
        data class TransportSell(val destination : String) : State()
        object NavigateBackToAsteroidField : State()
        object Refine : State()
    }
}