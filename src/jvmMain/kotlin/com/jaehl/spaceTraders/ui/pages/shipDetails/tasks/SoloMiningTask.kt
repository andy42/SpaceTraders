package com.jaehl.spaceTraders.ui.pages.shipDetails.tasks

import com.jaehl.spaceTraders.data.model.MiningSurvey
import com.jaehl.spaceTraders.data.model.Ship
import com.jaehl.spaceTraders.data.services.FleetService
import com.jaehl.spaceTraders.util.Logger
import kotlinx.coroutines.delay
import java.util.*
import javax.inject.Inject

class SoloMiningTask @Inject constructor(
    private val logger : Logger,
    private val fleetService : FleetService,
    private val basicTasks : BasicTasks
) {

    private fun isShipFull(ship : Ship) : Boolean {
        return (ship.cargo.units == ship.cargo.capacity)
    }

    private suspend fun mining(cargoShipId : String, miningSurvey : MiningSurvey, localSell : List<String>, remoteSell : List<String>) : Boolean{
        var cargoShip = fleetService.getShip(cargoShipId)

        cargoShip = basicTasks.mine(cargoShip, miningSurvey)

        if(isShipFull(cargoShip)){
            cargoShip = basicTasks.sell(cargoShip, localSell)
        }

        var minerFul = isShipFull(cargoShip)

        if(minerFul){
            cargoShip = basicTasks.enterOrbit(cargoShip)
            cargoShip = basicTasks.jettisonTask(cargoShip, remoteSell)
            cargoShip = basicTasks.dock(cargoShip)
        }

        return isShipFull(cargoShip)
    }

    suspend fun scanAndMine(cargoShipId : String, localSell : List<String>, remoteSell : List<String>) : Boolean{

        var cargoShip =fleetService.getShip(cargoShipId)
        delay(1000)

        val miningSurvey = basicTasks.surveyForTask(cargoShip, remoteSell)
        cargoShip = basicTasks.dock(cargoShip)

        logger.log("miningSurvey : ${miningSurvey.size.value}")
        miningSurvey.deposits.forEach {
            logger.log(it.symbol)
        }

        var miningIndex = 0
        while (miningSurvey.expiration.time > (Date().time + 1000)) {
            logger.log("miningIndex : ${miningIndex++}")
            if(mining(cargoShipId, miningSurvey, localSell, remoteSell)){
                return true
            }
        }
        basicTasks.enterOrbit(cargoShip)
        return false
    }

    private suspend fun mineOrder(cargoShipId : String, localSell : List<String>, remoteSell : List<String>, sellOrders : List<SellOrder>, asteroidField : String) {
        while(true) {
            if(scanAndMine(cargoShipId, localSell, remoteSell)){
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

    suspend fun startSellOrder(cargoShipId : String, sellOrders : List<SellOrder>, asteroidField : String, homeSystem : String) {
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
            mineOrder(cargoShipId, localSell, sellItemArray, sellOrders, asteroidField)
        }
    }

    private val asteroidFieldSellItems = listOf(
        "IRON_ORE",
        "COPPER_ORE",
        "SILVER_ORE",
        "GOLD_ORE",
        "SILICON_CRYSTALS",
        "ICE_WATER",
        "AMMONIA_ICE",
        "QUARTZ_SAND",
        "DIAMONDS",
        "PLATINUM_ORE"
    )
}