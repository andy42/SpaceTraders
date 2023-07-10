package com.jaehl.spaceTraders.data.controllers

import com.jaehl.spaceTraders.data.model.MiningSurvey
import com.jaehl.spaceTraders.data.model.Ship
import com.jaehl.spaceTraders.data.model.Transaction
import com.jaehl.spaceTraders.data.model.response.Cooldown
import com.jaehl.spaceTraders.data.services.ContractService
import com.jaehl.spaceTraders.data.services.FleetService
import com.jaehl.spaceTraders.util.DateHelper
import java.util.*

interface ShipController {
    fun getShip() : Ship
    fun getShipId() : String
    fun getCoolDown() : Cooldown
    fun setCoolDown(seconds : Int)
    fun survey() : List<MiningSurvey>
    fun survey(mostIncludeList: List<String>): MiningSurvey?
    fun refine (itemId : String)
    fun extract(miningSurvey : MiningSurvey?)
    fun sell(itemId : String, units : Int) : Transaction?
    fun sellAll(itemId : String) : Transaction?
    fun sellAll(itemIdList : List<String>): List<Transaction>
    fun purchase(itemId : String, units : Int) : Transaction
    fun contractDeliver(contractId : String, itemId : String, units : Int)
    fun dock()
    fun enterOrbit()
    fun refuel()
    fun jettison(itemId : String, units : Int)
    fun jettisonAllOfSingle(itemId : String)
    fun jettisonAllBut(keepList : List<String>)
    fun addCargo(itemId : String, units : Int) // is not backed by API, only use for the second ship of a transferCargo
    fun transferCargo(shipController : ShipController, itemId : String, units : Int)
    fun transferAllCargo(shipController : ShipController, itemId : String)
    fun transferAllCargo(shipController : ShipController, itemIdList : List<String>)
    fun navigate(destinationId : String)
    fun calculateNavState() : Ship.Nav.Statue
}

class ShipControllerImp(
    private var ship : Ship,
    private val fleetService : FleetService,
    private val contractService : ContractService,
    private val dateHelper : DateHelper
) : ShipController  {

    private var coolDown = initCoolDown()

    private fun initCoolDown() : Cooldown {
        val coolDown = fleetService.getShipCoolDown(ship.symbol) ?: Cooldown( expiration= dateHelper.getNow())
        if(ship.nav.route.arrival.time > coolDown.expiration.time) {
            return Cooldown( expiration= ship.nav.route.arrival)
        }
        return coolDown
    }

    override fun getShip() : Ship = ship

    override fun getShipId(): String {
        return ship.symbol
    }

    override fun getCoolDown(): Cooldown {
        return coolDown
    }

    override fun setCoolDown(seconds: Int) {
        coolDown = Cooldown(
            shipSymbol = ship.symbol,
            totalSeconds = seconds,
            remainingSeconds = seconds,
            expiration = dateHelper.getNowPlusMilliseconds(seconds*1000L)
        )
    }

    override fun survey(): List<MiningSurvey> {
        val response = fleetService.shipMiningSurvey(ship.symbol)
        coolDown = response.cooldown
        return response.surveys
    }

    override fun survey(mostIncludeList: List<String>): MiningSurvey? {
        survey().forEach { miningSurvey ->
            if(miningSurvey.deposits.firstOrNull{
                    mostIncludeList.contains(it.symbol)
            } != null) {
                return miningSurvey
            }
        }
        return null
    }

    override fun refine(itemId: String) {
        val response = fleetService.shipRefineMaterials(ship.symbol, itemId)
        coolDown = response.cooldown
        ship = ship.copy(
            cargo = response.cargo
        )
    }

    override fun extract(miningSurvey: MiningSurvey?) {
        val response = if(miningSurvey != null){
            fleetService.shipExtract(ship.symbol, miningSurvey)
        } else {
            fleetService.shipExtract(ship.symbol)
        }
        ship = ship.copy(
            cargo = response.cargo
        )
        coolDown = response.cooldown
    }

    override fun sell(itemId: String, units: Int) : Transaction? {
        ship.cargo.inventory.firstOrNull{it.symbol == itemId} ?: return null
        val response = fleetService.shipSellCargo(ship.symbol, itemId, units)
        ship = ship.copy(
            cargo = response.cargo
        )
        return response.transaction
    }

    override fun sellAll(itemId: String): Transaction? {
        val inventoryItem = ship.cargo.inventory.firstOrNull{it.symbol == itemId} ?: return null
        return sell(itemId, inventoryItem.units)
    }

    override fun sellAll(itemIdList : List<String>): List<Transaction> {

        val transactionList = mutableListOf<Transaction>()
        itemIdList.forEach { itemId ->
            sellAll(itemId)?.let { transaction ->
                transactionList.add(transaction)
            }
        }

        return transactionList
    }

    override fun purchase(itemId: String, units: Int) : Transaction{
        val response =fleetService.shipPurchaseCargo(ship.symbol, itemId, units)
        ship = ship.copy(
            cargo = response.cargo
        )
        return response.transaction
    }

    override fun contractDeliver(contractId: String, itemId: String, units: Int) {
        val response = contractService.contractDeliver(contractId, ship.symbol, itemId, units)
        ship = ship.copy(
            cargo = response.cargo
        )
    }

    override fun dock() {
        if(ship.nav.status == Ship.Nav.Statue.Docked) return
        val response =fleetService.shipDock(ship.symbol)
        ship = ship.copy(
            nav = response
        )
    }

    override fun enterOrbit() {
        if(ship.nav.status == Ship.Nav.Statue.InOrbit) return
        val response =fleetService.shipEnterOrbit(ship.symbol)
        ship = ship.copy(
            nav = response
        )
    }

    override fun refuel() {
        val response = fleetService.shipRefuel(ship.symbol)
        ship = ship.copy(
            fuel = response.fuel
        )
    }

    override fun jettison(itemId: String, units: Int) {
        val response = fleetService.shipJettisonCargo(ship.symbol, itemId, units)
        ship = ship.copy(
            cargo = response.cargo
        )
    }

    override fun jettisonAllOfSingle(itemId: String) {
        val inventoryItem = ship.cargo.inventory.firstOrNull{it.symbol == itemId} ?: return
        jettison(itemId, inventoryItem.units)
    }

    override fun jettisonAllBut(keepList: List<String>) {
        ship.cargo.inventory.forEach { inventoryItem ->
            if(keepList.contains(inventoryItem.symbol)) return@forEach
            jettisonAllOfSingle(inventoryItem.symbol)
        }
    }

    // is not backed by API, only use for the second ship of a transferCargo API request
    override fun addCargo(itemId: String, units: Int) {
        val cargoMutable = ship.cargo.inventory.toMutableList()
        if(ship.cargo.inventory.map { it.symbol }.contains(itemId)){
            val index = ship.cargo.inventory.indexOf(ship.cargo.inventory.first{ it.symbol == itemId})
            cargoMutable[index] = cargoMutable[index].copy(
                units = cargoMutable[index].units + units
            )
        } else {
            cargoMutable.add(Ship.Cargo.InventoryItem(
                symbol = itemId,
                name = itemId,
                description = "",
                units = units
            ))
        }
        ship = ship.copy(
            cargo = ship.cargo.copy(
                inventory = cargoMutable,
                units = ship.cargo.units + units
            )
        )
    }

    override fun transferCargo(shipController: ShipController, itemId: String, units: Int) {
        if((shipController.getShip().cargo.units + units) > shipController.getShip().cargo.capacity) return

        val cargo = fleetService.shipTransferCargo(ship.symbol, itemId, units, shipController.getShipId())
        ship = ship.copy(
            cargo = cargo
        )
        shipController.addCargo(itemId, units)
    }

    //will try to transfer as much as possible up to capacity
    override fun transferAllCargo(shipController: ShipController, itemId: String) {
        val inventoryItem = ship.cargo.inventory.firstOrNull{it.symbol == itemId} ?: return
        if((shipController.getShip().cargo.units + inventoryItem.units) > shipController.getShip().cargo.capacity) {
            val units = shipController.getShip().cargo.capacity - (shipController.getShip().cargo.units)
            if(units == 0) return
            transferCargo(shipController, itemId, units)
        } else {
            transferCargo(shipController, itemId, inventoryItem.units)
        }
    }

    //will try to transfer as much as possible up to capacity
    override fun transferAllCargo(shipController: ShipController, itemIdList: List<String>) {
        itemIdList.forEach { itemId ->
            transferAllCargo(shipController, itemId)
        }
    }

    override fun navigate(destinationId: String) {
        val response = fleetService.shipNavigate(ship.symbol,destinationId)
        ship = ship.copy(
            nav = response.nav,
            fuel = response.fuel
        )
        coolDown = Cooldown(
            expiration = ship.nav.route.arrival
        )
    }

    override fun calculateNavState(): Ship.Nav.Statue {
        if(ship.nav.status == Ship.Nav.Statue.InTransit){
            if(ship.nav.route.arrival <= dateHelper.getNow()){
                ship = ship.copy(
                    nav = ship.nav.copy(
                        status = Ship.Nav.Statue.InOrbit
                    )
                )
            }
        }
        return ship.nav.status
    }
}