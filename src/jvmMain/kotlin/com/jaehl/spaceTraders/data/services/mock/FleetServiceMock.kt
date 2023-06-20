package com.jaehl.spaceTraders.data.services.mock

import com.jaehl.spaceTraders.data.model.*
import com.jaehl.spaceTraders.data.model.response.*
import com.jaehl.spaceTraders.data.remote.BadRequest
import com.jaehl.spaceTraders.data.remote.ResourceNotFound
import com.jaehl.spaceTraders.data.remote.ResponsePageMeta
import com.jaehl.spaceTraders.data.remote.ResponsePaged
import com.jaehl.spaceTraders.data.services.FleetService
import com.jaehl.spaceTraders.util.DateHelper
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class FleetServiceMock(
    val agent : Agent,
    val transActions : ArrayList<Transaction>,
    val shipMap : HashMap<String, Ship>,
    val shipCoolDowns : HashMap<String, Cooldown?>,
    val refineDataMap : HashMap<String, RefineData>,
    val dateHelper : DateHelper
) : FleetService {

    var pricePerUnit = 2

    data class RefineData(
        val inputCargoId : String,
        val inputAmount : Int,
        val outputCargoId : String,
        val outputAmount : Int
    )

    private fun createResourceNotFoundException() = ResourceNotFound("Resource with the given identifier does not exist.")

    var extractYield = ExtractResponse.Yield(
        symbol = "COPPER_ORE",
        units = 10
    )

    var coolDownInterval : Int = 1

    private fun createCoolDown(shipId : String) : Cooldown{
        val coolDown =  Cooldown(
            shipSymbol = shipId,
            totalSeconds = coolDownInterval,
            remainingSeconds = coolDownInterval,
            expiration = dateHelper.getNowPlusMilliseconds(coolDownInterval*1000L)
        )
        shipCoolDowns[shipId] = coolDown
        return coolDown
    }

    override fun getShips(page: Int): ResponsePaged<Ship> {
        val list = shipMap.values.toList()
        return ResponsePaged(
            data = list,
            meta = ResponsePageMeta(
                total = list.size,
                page = 1,
                limit = 10
            )
        )
    }

    override fun getShip(shipId: String): Ship {
        return shipMap[shipId] ?: throw createResourceNotFoundException()
    }

    override fun getShipCoolDown(shipId: String): Cooldown? {
        return shipCoolDowns[shipId]
    }

    override fun shipEnterOrbit(shipId: String): Ship.Nav {
        val ship = shipMap[shipId] ?: throw createResourceNotFoundException()
        shipMap[shipId] = ship.copy(
            nav = ship.nav.copy(
                status = Ship.Nav.Statue.InOrbit
            )
        )
        return shipMap[shipId]?.nav ?: throw createResourceNotFoundException()
    }

    override fun shipDock(shipId: String): Ship.Nav {
        val ship = shipMap[shipId] ?: throw createResourceNotFoundException()
        shipMap[shipId] = ship.copy(
            nav = ship.nav.copy(
                status = Ship.Nav.Statue.Docked
            )
        )
        return shipMap[shipId]?.nav ?: throw createResourceNotFoundException()
    }

    override fun shipRefuel(shipId: String): RefuelResponse {
        val ship = shipMap[shipId] ?: throw createResourceNotFoundException()
        if(ship.nav.status != Ship.Nav.Statue.Docked){
            throw BadRequest("Ship action failed. Ship is not currently docked at ${ship.nav.waypointSymbol}")
        }
        val fuel = ship.fuel.copy(
            current =  ship.fuel.capacity
        )
        shipMap[shipId] = ship.copy(
            fuel = fuel
        )
        return RefuelResponse(
            agent = agent,
            fuel = fuel
        )
    }

    fun addRefineData(refineData : RefineData) {
        refineDataMap[refineData.outputCargoId] = refineData
    }

    override fun shipRefineMaterials(shipId: String, produce: String): RefineMaterialsResponse {
        var ship = shipMap[shipId] ?: throw createResourceNotFoundException()

        val refineData = refineDataMap[produce] ?: throw BadRequest("can not produce item")
        val cargoInput = ship.cargo.inventory.firstOrNull{it.symbol == refineData.inputCargoId} ?: throw BadRequest("not enough input items")
        if(cargoInput.units < refineData.inputAmount) throw BadRequest("not enough input items")
        if((ship.cargo.units + refineData.outputAmount - refineData.inputAmount) > ship.cargo.capacity) throw BadRequest("not enough room in cargo")


        ship = ship.copy(
            cargo = MockUtil.removeCargo(ship.cargo, refineData.inputCargoId, refineData.inputAmount)
        )

        ship = ship.copy(
            cargo = MockUtil.addCargo(ship.cargo, refineData.outputCargoId, refineData.outputAmount)
        )

        shipMap[shipId] = ship

        return RefineMaterialsResponse(
            cargo = ship.cargo,
            cooldown = createCoolDown(shipId),
            produced = listOf(
                RefineMaterialsResponse.Amount(
                    tradeSymbol = refineData.outputCargoId,
                    units = refineData.outputAmount
                )
            ),
            consumed = listOf(
                RefineMaterialsResponse.Amount(
                    tradeSymbol = refineData.inputCargoId,
                    units = refineData.inputAmount
                )
            )
        )
    }

//    private fun addCargo(cargo : List<Ship.Cargo.InventoryItem>, cargoId: String, units: Int) : List<Ship.Cargo.InventoryItem>{
//        val inventory = cargo.toMutableList()
//        val inventoryIndex = inventory.indexOfFirst { it.symbol == cargoId}
//        if(inventoryIndex == -1){
//            inventory.add(
//                Ship.Cargo.InventoryItem(
//                    symbol = cargoId,
//                    name = cargoId,
//                    description = cargoId,
//                    units = units
//                )
//            )
//        } else {
//            inventory[inventoryIndex] = inventory[inventoryIndex].copy(
//                units = inventory[inventoryIndex].units + extractYield.units
//            )
//        }
//        return inventory
//    }
//
//    private fun removeCargo(cargo : List<Ship.Cargo.InventoryItem>, cargoId: String, units: Int) : List<Ship.Cargo.InventoryItem>{
//        val inventory = cargo.toMutableList()
//        val inventoryIndex = inventory.indexOfFirst { it.symbol == cargoId}
//        if(inventoryIndex == -1){
//            throw Exception("cargo not found")
//        }
//
//        if((inventory[inventoryIndex].units - units) < 0){
//            throw Exception("not enough cargo")
//        }
//        else if((inventory[inventoryIndex].units - units) == 0){
//            inventory.removeAt(inventoryIndex)
//        }
//        else {
//            inventory[inventoryIndex] = inventory[inventoryIndex].copy(
//                units = inventory[inventoryIndex].units - units
//            )
//        }
//
//        return inventory
//    }

    override fun shipExtract(shipId: String): ExtractResponse {
        var ship = shipMap[shipId] ?: throw createResourceNotFoundException()

        //TODO("add cargo full error")

        ship = ship.copy(
            cargo = MockUtil.addCargo(ship.cargo, extractYield.symbol, extractYield.units)
//            cargo = ship.cargo.copy(
//                inventory = addCargo(ship.cargo.inventory, extractYield.symbol, extractYield.units),
//                units = ship.cargo.units + extractYield.units
//            )
        )
        shipMap[shipId] = ship

        return ExtractResponse(
            extraction = ExtractResponse.Extraction(
                shipSymbol = shipId,
                yield = extractYield
            ),
            cargo = ship.cargo,
            cooldown = createCoolDown(shipId)
        )
    }

    override fun shipExtract(shipId: String, miningSurvey: MiningSurvey): ExtractResponse {
        return shipExtract(shipId)
    }

    override fun shipNavigate(shipId: String, waypointId: String): ShipNavigateResponse {
        var ship = shipMap[shipId] ?: throw createResourceNotFoundException()
        //TODO("add not enough fuel error")
        ship = ship.copy(
            nav = ship.nav.copy(
                waypointSymbol = waypointId,
                status = Ship.Nav.Statue.InTransit,
                route = ship.nav.route.copy(
                    departure = SystemWaypoint(
                        symbol = ship.nav.waypointSymbol
                    ),
                    destination = SystemWaypoint(
                        symbol = waypointId
                    ),
                    departureTime = dateHelper.getNow(),
                    arrival = dateHelper.getNowPlusMilliseconds(coolDownInterval*1000L)
                )
            ),
            fuel = ship.fuel.copy(
                current = ship.fuel.current - 1
            )
        )
        shipMap[shipId] = ship

        return ShipNavigateResponse(
            fuel = ship.fuel,
            nav = ship.nav
        )
    }

    override fun shipWarp(shipId: String, waypointId: String): ShipWarpResponse {
        var ship = shipMap[shipId] ?: throw createResourceNotFoundException()
        //TODO("add not enough fuel error")
        ship = ship.copy(
            nav = ship.nav.copy(
                waypointSymbol = waypointId,
                status = Ship.Nav.Statue.InTransit,
                route = ship.nav.route.copy(
                    departure = SystemWaypoint(
                        symbol = ship.nav.waypointSymbol
                    ),
                    destination = SystemWaypoint(
                        symbol = waypointId
                    )
                )
            ),
            fuel = ship.fuel.copy(
                current = ship.fuel.current - 1
            )
        )
        shipMap[shipId] = ship

        return ShipWarpResponse(
            fuel = ship.fuel,
            nav = ship.nav
        )
    }

    override fun shipJump(shipId: String, systemId: String): ShipJumpResponse {
        var ship = shipMap[shipId] ?: throw createResourceNotFoundException()
        val newWaypoint = "$systemId-1"
        ship = ship.copy(
            nav = ship.nav.copy(
                systemSymbol = systemId,
                waypointSymbol = newWaypoint,
                status = Ship.Nav.Statue.InOrbit,
                route = ship.nav.route.copy(
                    departure = SystemWaypoint(
                        symbol = ship.nav.waypointSymbol
                    ),
                    destination = SystemWaypoint(
                        symbol = newWaypoint
                    )
                )
            )
        )
        shipMap[shipId] = ship
        return ShipJumpResponse(
            nav = ship.nav,
            cooldown = createCoolDown(shipId)
        )
    }

    override fun shipSellCargo(shipId: String, cargoId: String, units: Int): ShipSellCargoResponse {
        var ship = shipMap[shipId] ?: throw createResourceNotFoundException()

        ship = ship.copy(
            cargo = MockUtil.removeCargo(ship.cargo, cargoId, units)
//            cargo = ship.cargo.copy(
//                inventory = removeCargo(ship.cargo.inventory, cargoId, units),
//                units = ship.cargo.units - units
//            )
        )
        shipMap[shipId] = ship

        val transaction = Transaction(
            waypointSymbol = ship.nav.waypointSymbol,
            shipSymbol = shipId,
            tradeSymbol = cargoId,
            type = Transaction.Type.Sell,
            units = units,
            pricePerUnit = pricePerUnit,
            totalPrice = pricePerUnit*units,
            timestamp = Date()
        )

        transActions.add(transaction)

        return ShipSellCargoResponse(
            agent = agent,
            cargo = ship.cargo,
            transaction = transaction
        )
    }

    override fun shipPurchaseCargo(shipId: String, cargoId: String, units: Int): ShipPurchaseCargoResponse {
        var ship = shipMap[shipId] ?: throw createResourceNotFoundException()

        ship = ship.copy(
            cargo = MockUtil.addCargo(ship.cargo, cargoId, units)
//            cargo = ship.cargo.copy(
//                inventory = addCargo(ship.cargo.inventory, cargoId, units),
//                units = ship.cargo.units + units
//            )
        )
        shipMap[shipId] = ship

        val pricePerUnit = 2

        val transaction = Transaction(
            waypointSymbol = ship.nav.waypointSymbol,
            shipSymbol = shipId,
            tradeSymbol = cargoId,
            type = Transaction.Type.Purchase,
            units = units,
            pricePerUnit = pricePerUnit,
            totalPrice = pricePerUnit*units,
            timestamp = Date()
        )
        transActions.add(transaction)

        return ShipPurchaseCargoResponse(
            agent = agent,
            cargo = ship.cargo,
            transaction = transaction
        )
    }

    override fun shipJettisonCargo(shipId: String, cargoId: String, units: Int): ShipJettisonCargoResponse {
        var ship = shipMap[shipId] ?: throw createResourceNotFoundException()

        ship = ship.copy(
            cargo = MockUtil.removeCargo(ship.cargo, cargoId, units)
//            cargo = ship.cargo.copy(
//                inventory = removeCargo(ship.cargo.inventory, cargoId, units),
//                units = ship.cargo.units - units
//            )
        )
        shipMap[shipId] = ship

        return ShipJettisonCargoResponse(
            cargo = ship.cargo
        )
    }

    override fun shipMiningSurvey(shipId: String): ShipMiningSurveyResponse {
        return ShipMiningSurveyResponse(
            cooldown = createCoolDown(shipId),
            surveys = listOf(
                MiningSurvey()
            )
        )
    }

    override fun shipTransferCargo(
        shipId: String,
        cargoId: String,
        units: Int,
        transferToShipSymbol: String
    ): Ship.Cargo {
        var ship = shipMap[shipId] ?: throw createResourceNotFoundException()
        val transferToShip = shipMap[transferToShipSymbol] ?: throw createResourceNotFoundException()

        ship = ship.copy(
            cargo = MockUtil.removeCargo(ship.cargo, cargoId, units)
//            cargo = ship.cargo.copy(
//                inventory = removeCargo(ship.cargo.inventory, cargoId, units)
//            )
        )
        shipMap[shipId] = ship

        shipMap[transferToShipSymbol] = transferToShip.copy(
            cargo = MockUtil.addCargo(transferToShip.cargo, cargoId, units)
//            cargo = transferToShip.cargo.copy(
//                inventory = removeCargo(transferToShip.cargo.inventory, cargoId, units)
//            )
        )

        return ship.cargo
    }
}