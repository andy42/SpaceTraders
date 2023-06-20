package com.jaehl.spaceTraders.ui.pages.shipDetails.tasks

import com.jaehl.spaceTraders.data.model.*
import com.jaehl.spaceTraders.data.model.response.Cooldown
import com.jaehl.spaceTraders.data.model.response.ExtractResponse
import com.jaehl.spaceTraders.data.model.response.JumpGateResponse
import com.jaehl.spaceTraders.data.model.response.ShipMiningSurveyResponse
import com.jaehl.spaceTraders.data.services.ContractService
import com.jaehl.spaceTraders.data.services.FleetService
import com.jaehl.spaceTraders.data.services.SystemService
import com.jaehl.spaceTraders.util.Logger
import kotlinx.coroutines.delay
import java.util.*
import javax.inject.Inject

class BasicTasks @Inject constructor(
    private val logger : Logger,
    private val fleetService : FleetService,
    private val contractService : ContractService,
    private val systemService: SystemService
) {
    suspend fun getJumpGate(systemId: String, waypointId: String) : JumpGateResponse {
        val response = systemService.getJumpGate(systemId, waypointId)
        return response
    }

    suspend fun getSystemWaypoints(systemId : String) : List<SystemWaypoint> {
        val response = systemService.getSystemWaypoints(systemId, 1)
        return response.data
    }

    suspend fun getMarket(systemId : String, waypointId: String) : Market {
        val response = systemService.getMarket(systemId, waypointId)
        return response
    }

    suspend fun surveyTask(ship : Ship) : ShipMiningSurveyResponse {
        val response = fleetService.shipMiningSurvey(ship.symbol)
        val delayTime = (response.cooldown.expiration.time - Date().time)
        delay(delayTime)
        return response
    }

    suspend fun surveyForTask(ship : Ship, items : List<String>) : MiningSurvey {
        var currentShip = ship

        var surveyCount = 0
        while (true){
            surveyCount++
            logger.log("survey start : $surveyCount")
            val response = surveyTask(currentShip)
            if(response.surveys.firstOrNull()?.deposits?.firstOrNull { items.contains(it.symbol) } == null) continue
            return response.surveys.first()
        }
    }

    suspend fun surveyWithoutDelay(ship : Ship, items : List<String>) : TaskResponse<MiningSurvey?> {
        val response = fleetService.shipMiningSurvey(ship.symbol)
        logger.log("${ship.symbol} survey : ${response.surveys.first().deposits.map { it.symbol }}")
        return if(response.surveys.firstOrNull()?.deposits?.firstOrNull { items.contains(it.symbol) } == null) {
            TaskResponse(
                data = null,
                cooldown = response.cooldown
            )
        } else {
            TaskResponse(
                data = response.surveys.first(),
                cooldown = response.cooldown
            )
        }
    }

    suspend fun refine(ship : Ship, itemId : String) : Pair<Ship, Cooldown> {
        val response = fleetService.shipRefineMaterials(ship.symbol, itemId)
        logger.log("${ship.symbol} : refine itemId:$itemId")
        return Pair(
            ship.copy(
                cargo = response.cargo
            ),
            response.cooldown
        )
    }

    suspend fun mineWithoutDelay(ship : Ship, miningSurvey : MiningSurvey) : TaskResponse<Ship> {
        var currentShip = ship
        try {
            logger.log("mining : ${ship.symbol}")
            val response = fleetService.shipExtract(ship.symbol, miningSurvey)
            return TaskResponse<Ship>(
                data = currentShip.copy(
                    cargo = response.cargo
                ),
                cooldown = response.cooldown
            )
        }
        catch (t : Throwable) {
            logger.log("mining error : ${ship.symbol} full")
            return TaskResponse(
                data = fleetService.getShip(currentShip.symbol),
                cooldown = Cooldown()
            )
        }
    }

    suspend fun mineWithoutDelay(ship : Ship) : TaskResponse<Ship> {
        var currentShip = ship
        try {
            logger.log("mining : ${ship.symbol}")
            val response = fleetService.shipExtract(ship.symbol)
            return TaskResponse<Ship>(
                data = currentShip.copy(
                    cargo = response.cargo
                ),
                cooldown = response.cooldown
            )
        }
        catch (t : Throwable) {
            logger.log("mining error : ${ship.symbol} full")
            return TaskResponse(
                data = fleetService.getShip(currentShip.symbol),
                cooldown = Cooldown()
            )
        }
    }

    suspend fun mine(ship : Ship, miningSurvey : MiningSurvey) : Ship {
        try {
            val response = fleetService.shipExtract(ship.symbol, miningSurvey)
            val delay = response.cooldown.expiration.time - Date().time
            delay(delay)

            return ship.copy(
                cargo = response.cargo
            )
        }
        catch (t : Throwable) {
            logger.log("mining error : ${ship.symbol} full")
            return ship
        }
    }

    suspend fun mine(
        ship : Ship,
        resultsCallBack : ((extractResponse : ExtractResponse) -> Unit)? = null
    ) : Ship {
        try {
            val response = fleetService.shipExtract(ship.symbol)
            val delay = response.cooldown.expiration.time - Date().time
            resultsCallBack?.invoke(response)
            delay(delay)

            return ship.copy(
                cargo = response.cargo
            )
        }
        catch (t : Throwable) {
            logger.log("mining error : ${ship.symbol} full")
            return ship
        }
    }

    suspend fun sell(ship : Ship, sellItems : List<String>) : Ship {
        var currentShip = ship

        sellItems.forEach {sellId ->
            currentShip.cargo.inventory.firstOrNull {it.symbol == sellId }?.let {
                val response =fleetService.shipSellCargo(ship.symbol, it.symbol, it.units)
                logger.log("${ship.symbol} : sell, ${it.symbol}, units:${it.units}, pricePerUnit:${response.transaction.pricePerUnit}, total:${response.transaction.totalPrice}")
                currentShip = currentShip.copy(
                    cargo = response.cargo
                )
            }
        }
        return currentShip
    }

    suspend fun sell(ship : Ship, item : String, amount : Int) : Ship {
        var currentShip = ship

        val response =fleetService.shipSellCargo(ship.symbol, item, amount)
        logger.log("${ship.symbol} : sell, $item, units:${amount}, pricePerUnit:${response.transaction.pricePerUnit}, total:${response.transaction.totalPrice}")
        return currentShip.copy(
            cargo = response.cargo
        )
    }

    suspend fun purchase(ship : Ship, item : String, amount : Int) : Pair<Ship, Transaction> {
        val response =fleetService.shipPurchaseCargo(ship.symbol, item, amount)
        logger.log("${ship.symbol} : purchase, units:${amount}, pricePerUnit:${response.transaction.pricePerUnit}, total:${response.transaction.totalPrice}")
        return Pair(
            ship.copy(
                cargo = response.cargo
            ),
            response.transaction
        )
    }

    suspend fun contractDeliver(contractId : String, ship : Ship, item : String, amount : Int) : Pair<Ship, Contract> {
        var currentShip = ship
        val response = contractService.contractDeliver(contractId, ship.symbol, item, amount)
        val deliverData = response.contract.terms.deliver.first()
        logger.log("${ship.symbol} : contractDeliver left:${deliverData.unitsRequired - deliverData.unitsFulfilled}")
        return Pair(
            currentShip.copy(
                cargo = response.cargo
            ),
            response.contract
        )
    }

    suspend fun dock(ship : Ship) : Ship{
        var currentShip = ship
        val navOrbit =fleetService.shipDock(ship.symbol)
        currentShip = currentShip.copy(
            nav = navOrbit
        )
        return currentShip
    }

    suspend fun enterOrbit(ship : Ship) : Ship{
        var currentShip = ship
        val navOrbit =fleetService.shipEnterOrbit(ship.symbol)
        currentShip = currentShip.copy(
            nav = navOrbit
        )
        return currentShip
    }

    suspend fun shipRefuelTask(ship : Ship) : Ship{
        val response = fleetService.shipRefuel(ship.symbol)
        logger.log("shipRefuelTask ship : ${ship.symbol}")
        return ship.copy(
            fuel = response.fuel
        )
    }

    suspend fun jettisonTask(ship : Ship, keepList : List<String>)  : Ship {
        var currentShip = ship

        while (true){
            val inventoryItem = currentShip.cargo.inventory.firstOrNull { !keepList.contains(it.symbol) && it.symbol != "ANTIMATTER"} ?: break
            val response = fleetService.shipJettisonCargo(ship.symbol, inventoryItem.symbol, inventoryItem.units)
            logger.log("${ship.symbol} jettison : ${inventoryItem.symbol}")
            currentShip = currentShip.copy(
                cargo = response.cargo
            )
        }
        return currentShip
    }

    private fun addCargo(cargo : Ship.Cargo, inventoryItem : Ship.Cargo.InventoryItem) : Ship.Cargo {
        val cargoMutable = cargo.inventory.toMutableList()
        if(cargo.inventory.map { it.symbol }.contains(inventoryItem.symbol)){
            val index = cargo.inventory.indexOf(cargo.inventory.first{ it.symbol == inventoryItem.symbol})
            cargoMutable[index] = cargoMutable[index].copy(
                units = cargoMutable[index].units + inventoryItem.units
            )
        } else {
            cargoMutable.add(inventoryItem)
        }
        return return cargo.copy(
            inventory = cargoMutable,
            units = cargo.units + inventoryItem.units
        )
    }

    fun transferTask(ship1 : Ship, ship2 : Ship, cargoSearch : String) : Pair<Ship, Ship> {
        try {
            val inventoryItem = ship1.cargo.inventory.firstOrNull {
                it.symbol == cargoSearch && it.symbol != "ANTIMATTER"
            } ?: return Pair(ship1, ship2)

            var cargoSpace = (ship2.cargo.capacity - ship2.cargo.units)
            var units = inventoryItem.units
            if (units > cargoSpace) {
                units = cargoSpace
            }
            if (units == 0) return Pair(ship1, ship2)
            logger.log("${ship1.symbol} transfer :: inventoryItem ${inventoryItem.symbol} : $units")

            val cargo = fleetService.shipTransferCargo(ship1.symbol, inventoryItem.symbol, units, ship2.symbol)
            return Pair(
                ship1.copy(
                    cargo = cargo
                ),
                ship2.copy(
                    cargo = addCargo(
                        cargo = ship2.cargo,
                        inventoryItem = inventoryItem.copy(
                            units = units
                        )
                    )
                )
            )
        } catch (t : Throwable) {
            logger.log("${ship1.symbol} transfer :: error ${t.message}")
            return Pair(
                fleetService.getShip(ship1.symbol),
                fleetService.getShip(ship2.symbol)
            )
        }
    }

    suspend fun navigateTask(ship : Ship, destinationId : String) : Ship{
        val response = fleetService.shipNavigate(ship.symbol,destinationId)
        val delayTime = (response.nav.route.arrival.time - Date().time)
        logger.log("navigateTask ship : ${ship.symbol}, destinationId : $destinationId")
        delay(delayTime)
        return ship.copy(
            nav = response.nav,
            fuel = response.fuel
        )
    }

    suspend fun navigateWithoutDelay(ship : Ship, destinationId : String) : Ship{
        logger.log("navigateTask ship : ${ship.symbol}, destinationId : $destinationId")
        val response = fleetService.shipNavigate(ship.symbol,destinationId)
        return ship.copy(
            nav = response.nav,
            fuel = response.fuel
        )
    }

    suspend fun getShip(shipId : String) : Ship{
        val reponse = fleetService.getShip(shipId = shipId)
        logger.log("$shipId getShip")
        return reponse
    }


    suspend fun getShipCoolDown(shipId : String) : Cooldown?{
        val response = fleetService.getShipCoolDown(shipId = shipId)
        logger.log("$shipId getShipCoolDown")
        return response
    }
}