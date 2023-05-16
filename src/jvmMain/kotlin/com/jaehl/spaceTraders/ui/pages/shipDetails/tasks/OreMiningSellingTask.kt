package com.jaehl.spaceTraders.ui.pages.shipDetails.tasks

import com.jaehl.spaceTraders.data.model.MiningSurvey
import com.jaehl.spaceTraders.data.model.Ship
import com.jaehl.spaceTraders.data.model.response.Cooldown
import com.jaehl.spaceTraders.data.services.FleetService
import com.jaehl.spaceTraders.util.Logger
import kotlinx.coroutines.delay
import java.util.*
import javax.inject.Inject

class OreMiningSellingTask @Inject constructor(
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

    suspend fun start(asteroid : SellOrder, sellOrders : List<SellOrder>) {

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
                canSurvey = false,
                canTransport = true,
                canMine = false
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
                ship = basicTasks.getShip("TANGO42-5",),
                canSurvey = true
            ),
            ShipState(
                ship = basicTasks.getShip("TANGO42-6",)
            )
        )

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
            if(!it.coolDown.isFinished() && it.coolDown.getDelay() < delayTime){
                delayTime = it.coolDown.getDelay()
            }
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

    private suspend fun transferTask(ship1 : Ship, ship2 : Ship, cargoSearch : String) : Ship {
        var currentShip = ship1

        try {
            val inventoryItem = currentShip.cargo.inventory.firstOrNull {
                    it.symbol == cargoSearch && it.symbol != "ANTIMATTER"
                } ?: return currentShip

            var cargoSpace = (ship2.cargo.capacity - ship2.cargo.units)
            var units = inventoryItem.units
            if (units > cargoSpace) {
                units = cargoSpace
            }
            if (units == 0) return currentShip
            logger.log("${ship1.symbol} transfer :: inventoryItem ${inventoryItem.symbol} : $units")
            val cargo = fleetService.shipTransferCargo(ship1.symbol, inventoryItem.symbol, units, ship2.symbol)
            delay(1000)
            currentShip = currentShip.copy(
                cargo = cargo
            )
        } catch (t : Throwable) {
            logger.log("${ship1.symbol} transfer :: error")
            delay(1000)
            return ship1
        }
        return currentShip
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
        val canMine : Boolean = true,
        var coolDown: Cooldown = Cooldown(),
        var state : State = State.Mining
    ) {
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

                    localKeep.forEach {
                        val updatedShip = transferTask(ship, cargoShip.ship, it)
                        if(ship.cargo.units != updatedShip.cargo.units){
                            ship = updatedShip
                            cargoShip.ship = basicTasks.getShip(cargoShip.ship.symbol)
                            delay(1000)
                        }
                    }
                }
                is State.Surveying -> {
                    if (!coolDown.isFinished()) return
                    val reponse = basicTasks.surveyWithoutDelay(ship, localKeep)
                    coolDown = reponse.cooldown
                    if(reponse.data != null){
                        miningSurvey = reponse.data
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

                else -> {
                    logger.error("calculateState : error ${ship.symbol}")
                }
            }
        }
    }

    private sealed class State {
        object Mining : State()
        object Surveying : State()
        data class TransportSell(val destination : String) : State()
        object NavigateBackToAsteroidField : State()
    }
}