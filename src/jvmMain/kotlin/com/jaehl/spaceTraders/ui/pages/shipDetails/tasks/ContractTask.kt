package com.jaehl.spaceTraders.ui.pages.shipDetails.tasks

import com.jaehl.spaceTraders.data.model.MiningSurvey
import com.jaehl.spaceTraders.data.model.Ship
import com.jaehl.spaceTraders.data.model.response.Cooldown
import com.jaehl.spaceTraders.data.services.ContractService
import com.jaehl.spaceTraders.data.services.FleetService
import com.jaehl.spaceTraders.util.Logger
import kotlinx.coroutines.delay
import java.util.*
import javax.inject.Inject

class ContractTask @Inject constructor(
    private val logger : Logger,
    private val fleetService : FleetService,
    private val contractService : ContractService,
    private val basicTasks : BasicTasks
) {

    private var shipStatesList : List<ShipState> = listOf()

    lateinit var buyMarket: String
    lateinit var contactId : String
    lateinit var contactItem : String
    lateinit var contactLocation : String

    lateinit var asteroidField : String

    private var contractUnitsLeft = Int.MAX_VALUE

    suspend fun start(buyMarket : String, contactId : String, contactLocation : String,  contactItem : String) {

        this.buyMarket = buyMarket
        this.contactId = contactId
        this.contactLocation = contactLocation
        this.contactItem = contactItem



        shipStatesList = listOf(
            ShipState(
                ship = basicTasks.getShip("TANGO42-1")
            ),
            ShipState(
                ship = basicTasks.getShip("TANGO42-5")
            ),
            ShipState(
                ship = basicTasks.getShip("TANGO42-6")
            )
        )
        val deliverData = contractService.getContract(contactId).terms.deliver.first()
        delay(1000)
        contractUnitsLeft = (deliverData.unitsRequired - deliverData.unitsFulfilled)

        while (true) {
            shipStatesList.forEach {
                if(it.calculateState()) {
                    logger.log("Finished")
                    return
                }
            }
            var delayTime = getSmallestCoolDownDelay()
            logger.log("delayTime : ${(delayTime/1000).toInt()}")
            delay(delayTime)
        }
    }

    private fun getSmallestCoolDownDelay() : Long{
        var delayTime = Long.MAX_VALUE
        shipStatesList.forEach {
            if(it.coolDown.isFinished()){
                return 0
            }
            if( it.coolDown.getDelay() < delayTime){
                delayTime = it.coolDown.getDelay()
            }
        }
        return delayTime
    }

    private inner class ShipState(
        var ship : Ship,
        val canSurvey : Boolean = false,
        val canTransport : Boolean = false,
        val canMine : Boolean = true,
        var coolDown: Cooldown = Cooldown(),
        var state : State = State.TransportBuy
    ) {
        suspend fun calculateState() : Boolean {
            when(val currentState = state){
                is State.TransportBuy -> {
                    if(!coolDown.isFinished()) return false
                    ship = basicTasks.dock(ship)
                    val unitsToBuy = ship.cargo.capacity - ship.cargo.units
                    val response = basicTasks.purchase(ship, contactItem, unitsToBuy)
                    ship = response.first
                    if(response.second.pricePerUnit > 25){
                        logger.log("Price to high : ${response.second.pricePerUnit}")
                        return true
                    }
                    if(ship.fuel.current < 100){
                        ship = basicTasks.shipRefuelTask(ship)
                    }
                    ship = basicTasks.enterOrbit(ship)
                    ship = basicTasks.navigateWithoutDelay(ship, contactLocation)
                    coolDown = Cooldown(
                        expiration = ship.nav.route.arrival
                    )
                    state = State.TransportDeliver
                }
                is State.TransportDeliver -> {
                    if(!coolDown.isFinished()) return false
                    ship = basicTasks.dock(ship)
                    var unitsToDeliver = ship.cargo.capacity - ship.cargo.units
                    if(unitsToDeliver > contractUnitsLeft){
                        unitsToDeliver = contractUnitsLeft
                    }

                    val response = basicTasks.contractDeliver(contactId, ship, contactItem, unitsToDeliver)
                    ship = response.first
                    val deliverData = response.second.terms.deliver.first()
                    if((deliverData.unitsRequired - deliverData.unitsFulfilled) == 0) {
                        contractService.contractFulfill(contactId)
                        delay(1000)
                        return true
                    }
                    if((deliverData.unitsRequired - deliverData.unitsFulfilled) < 60){
                        contractUnitsLeft = (deliverData.unitsRequired - deliverData.unitsFulfilled)
                    }
                    if(ship.fuel.current < 100){
                        ship = basicTasks.shipRefuelTask(ship)
                    }
                    ship = basicTasks.enterOrbit(ship)
                    ship = basicTasks.navigateWithoutDelay(ship, buyMarket)
                    coolDown = Cooldown(
                        expiration = ship.nav.route.arrival
                    )
                    state = State.TransportBuy
                }
                else -> {
                    logger.error("calculateState : error ${ship.symbol}")
                }
            }
            return false
        }
    }

    private sealed class State {
        object TransportDeliver : State()
        object TransportBuy : State()
    }
}