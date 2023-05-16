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

class Task @Inject constructor(
    private val logger : Logger,
    private val fleetService : FleetService,
    private val contractService : ContractService,
    private val basicTasks : BasicTasks
) {
    private fun getGreaterTime(cooldown1: Cooldown, cooldown2: Cooldown) : Cooldown {
        if(cooldown1.expiration.time > cooldown2.expiration.time){
            return cooldown1
        } else {
            return cooldown2
        }
    }

    private fun isShipFull(ship : Ship) : Boolean {
        return (ship.cargo.units == ship.cargo.capacity)
    }

    private suspend fun transferTask(ship1 : Ship, ship2 : Ship, cargoSearch : String) : Ship {
        var currentShip = ship1

        try {
            val inventoryItem =
                currentShip.cargo.inventory.firstOrNull { it.symbol == cargoSearch && it.symbol != "ANTIMATTER" }
                    ?: return currentShip
            var cargoSpace = (ship2.cargo.capacity - ship2.cargo.units)
            var units = inventoryItem.units
            if (units > cargoSpace) {
                units = cargoSpace
            }
            if (units == 0) return currentShip
            logger.log("${ship1.symbol} transfer :: inventoryItem ${inventoryItem.symbol} : $units")
            val cargo = fleetService.shipTransferCargo(ship1.symbol, inventoryItem.symbol, units, ship2.symbol)
            currentShip = currentShip.copy(
                cargo = cargo
            )
        } catch (t : Throwable) {
            logger.log("${ship1.symbol} transfer :: error")
            return ship1
        }
        delay(1000)
        return currentShip
    }

    suspend fun groupMining(cargoShipId : String, minerShipIds : List<String>, miningSurvey : MiningSurvey, localSell : List<String>, remoteSell : List<String>) : Boolean{
        var cargoShip = fleetService.getShip(cargoShipId)

        var minerShips = minerShipIds.map {
            delay(1000)
            fleetService.getShip(it)
        }.toMutableList()
        delay(1000)

        val mine1 = basicTasks.mineWithoutDelay(cargoShip, miningSurvey)
        cargoShip =mine1.data

        var coolDown = mine1.cooldown

        minerShips.forEachIndexed { index, ship ->
            val mine = basicTasks.mineWithoutDelay(minerShips[index], miningSurvey)
            coolDown = getGreaterTime(coolDown, mine.cooldown)
            minerShips[index] = mine.data
        }

        delay(coolDown.expiration.time - Date().time)

        if(isShipFull(cargoShip)){
            cargoShip = basicTasks.sell(cargoShip, localSell)
        }


        var minerFul = isShipFull(cargoShip)
        minerShips.forEachIndexed { index, ship ->
            if(isShipFull(ship)) {
                minerShips[index] = basicTasks.sell(ship, localSell)
            }
            if(isShipFull(ship)) {
                remoteSell.forEach {
                    minerShips[index] = transferTask(minerShips[index], cargoShip, it)
                    cargoShip = fleetService.getShip(cargoShipId)
                    delay(1000)
                }

                minerFul = isShipFull(cargoShip) || minerFul
            }

            minerFul = isShipFull(minerShips[index]) || minerFul
        }

        if(minerFul){
            cargoShip = basicTasks.enterOrbit(cargoShip)
            minerShips.forEachIndexed { index, ship ->
                minerShips[index] = basicTasks.enterOrbit(ship)
            }

            cargoShip = basicTasks.jettisonTask(cargoShip, remoteSell)
            minerShips.forEachIndexed { index, ship ->
                minerShips[index] = basicTasks.jettisonTask(ship, remoteSell)
            }

            cargoShip = basicTasks.dock(cargoShip)
            minerShips.forEachIndexed { index, ship ->
                minerShips[index] = basicTasks.dock(ship)
            }
        }

        return isShipFull(cargoShip)
    }
//
    suspend fun scanAndMine(cargoShipId : String, minerShipIds : List<String>, localSell : List<String>, remoteSell : List<String>) : Boolean{

        var cargoShip =fleetService.getShip(cargoShipId)
        delay(1000)
        var minerShips = minerShipIds.map {
            fleetService.getShip(it)
            delay(1000)
        }

        val miningSurvey = basicTasks.surveyForTask(cargoShip, remoteSell)
        cargoShip = basicTasks.dock(cargoShip)

        logger.log("miningSurvey : ${miningSurvey.size.value}")
        miningSurvey.deposits.forEach {
            logger.log(it.symbol)
        }
        var miningIndex = 0
        while (miningSurvey.expiration.time > (Date().time + 1000)) {
            logger.log("miningIndex : ${miningIndex++}")
            if(groupMining(cargoShipId, minerShipIds, miningSurvey, localSell, remoteSell)){
                return true
            }
        }
        basicTasks.enterOrbit(cargoShip)
        return false
    }

    private suspend fun mineOrder(cargoShipId : String, minerShipIds : List<String>, localSell : List<String>, remoteSell : List<String>, sellOrders : List<SellOrder>, asteroidField : String) {
        while(true) {
            if(scanAndMine(cargoShipId, minerShipIds, localSell, remoteSell)){
                    break
            }
        }
        var cargoShip =fleetService.getShip(cargoShipId)
        if(cargoShip.fuel.current < 100){
            cargoShip = basicTasks.shipRefuelTask(cargoShip)
        }
        cargoShip = basicTasks.enterOrbit(cargoShip)
        sellOrders.forEach { sellOrder ->
            logger.log("sellOrder : ${sellOrder.destinationId}")
            cargoShip = basicTasks.navigateTask(cargoShip, sellOrder.destinationId)
            cargoShip = basicTasks.dock(cargoShip)
            if(cargoShip.fuel.current < 100){
                cargoShip = basicTasks.shipRefuelTask(cargoShip)
            }
            cargoShip = basicTasks.sell(cargoShip, sellOrder.items)
        }
        cargoShip = basicTasks.navigateTask(cargoShip, asteroidField)
    }

    suspend fun startSellOrder(cargoShipId : String, shipIds : List<String>, sellOrders : List<SellOrder>, asteroidField : String, homeSystem : String) {
        val sellItemArray = arrayListOf<String>()
        sellOrders.forEach {sellOder ->
            sellOder.items.forEach {
                sellItemArray.add(it)
            }
        }
        sellItemArray.toSet().toList()

        var localSell = arrayListOf<String>()
        asteroidFieldSellItems.forEach {
            if(!sellItemArray.contains(it)){
                localSell.add(it)
            }
        }

        while(true) {
            mineOrder(cargoShipId, shipIds, localSell, sellItemArray, sellOrders, asteroidField)
        }
    }

    private val asteroidFieldSellItems = listOf(
        "IRON_ORE",
        "COPPER_ORE",
        "ALUMINUM_ORE",
        "SILVER_ORE",
        "GOLD_ORE",
        "PLATINUM_ORE",
        "SILICON_CRYSTALS",
        "ICE_WATER",
        "AMMONIA_ICE",
        "QUARTZ_SAND",
        "DIAMONDS"
    )
}


class TaskResponse<T>(val data : T, val cooldown: Cooldown)

class SellOrder(
    val destinationId : String,
    val warpRequired : Boolean = false,
    val items : List<String> = listOf()
)