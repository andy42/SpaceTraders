package com.jaehl.spaceTraders.data.services.mock

import com.jaehl.spaceTraders.data.model.Agent
import com.jaehl.spaceTraders.data.model.Ship
import com.jaehl.spaceTraders.data.model.Transaction
import com.jaehl.spaceTraders.data.model.response.Cooldown
import com.jaehl.spaceTraders.data.model.response.ExtractResponse
import com.jaehl.spaceTraders.data.remote.BadRequest
import com.jaehl.spaceTraders.data.remote.ResourceNotFound
import com.jaehl.spaceTraders.util.DateHelperMock
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import kotlin.reflect.KFunction
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.jvm.isAccessible
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FleetServiceMockTest {

    private var agent = Agent()
    private val shipMap = HashMap<String, Ship>()
    private val shipCoolDowns = HashMap<String, Cooldown?>()
    private val refineDataMap = HashMap<String, FleetServiceMock.RefineData>()
    private val transActions = ArrayList<Transaction>()
    private val dateHelperMock = DateHelperMock()

    private val fleetServiceMock = FleetServiceMock(
        agent = agent,
        shipMap = shipMap,
        transActions = transActions,
        shipCoolDowns = shipCoolDowns,
        refineDataMap = refineDataMap,
        dateHelper = dateHelperMock
    )

    private fun clear(){
        agent = Agent()
        shipMap.clear()
        transActions.clear()
        shipCoolDowns.clear()
        refineDataMap.clear()
    }


    private fun addShip(ship : Ship){
        shipMap[ship.symbol] = ship
        shipCoolDowns[ship.symbol] = null
    }
    
    @Test
    fun `getShips returns matching ship`(){
        clear()

        val shipId = "Test"
        addShip(
            Shipbuilder(dateHelperMock)
                .setSymbol(shipId)
                .build()
        )

        val ship = fleetServiceMock.getShip(shipId)
        assertEquals(shipId, ship.symbol)
    }

    @Test
    fun `getShips with none valid id throws ResourceNotFound`(){
        clear()
        assertThrows<ResourceNotFound> {
            val ship = fleetServiceMock.getShip("test2")
        }
    }

    @Test
    fun `shipEnterOrbit`() {
        clear()

        val shipId = "Test"
        addShip(
            Shipbuilder(dateHelperMock)
                .setSymbol(shipId)
                .setNavStatus(Ship.Nav.Statue.Docked)
                .build()
        )

        val nav = fleetServiceMock.shipEnterOrbit(shipId)
        assertEquals(Ship.Nav.Statue.InOrbit, nav.status)

        val ship = fleetServiceMock.getShip(shipId)
        assertEquals(Ship.Nav.Statue.InOrbit, ship.nav.status)
    }

    @Test
    fun `shipDock`() {
        clear()

        val shipId = "Test"
        addShip(
            Shipbuilder(dateHelperMock)
                .setSymbol(shipId)
                .setNavStatus(Ship.Nav.Statue.InOrbit)
                .build()
        )

        val nav = fleetServiceMock.shipDock(shipId)
        assertEquals(Ship.Nav.Statue.Docked, nav.status)

        val ship = fleetServiceMock.getShip(shipId)
        assertEquals(Ship.Nav.Statue.Docked, ship.nav.status)
    }

    @Test
    fun `shipRefuel`() {
        clear()

        val shipId = "Test"
        addShip(
            Shipbuilder(dateHelperMock)
                .setSymbol(shipId)
                .setNavStatus(Ship.Nav.Statue.Docked)
                .setFuel(
                    Ship.Fuel(
                    current = 50,
                    capacity = 100
                ))
                .build()
        )

        val refuelResponse = fleetServiceMock.shipRefuel(shipId)
        assertEquals(100, refuelResponse.fuel.current)

        val ship = fleetServiceMock.getShip(shipId)
        assertEquals(100, ship.fuel.current)
    }

    private fun callPrivateMethod(objectInstance: Any, methodName: String, vararg args: Any?) : Any? {
        val privateMethod: KFunction<*>?  = objectInstance::class.memberFunctions.find { it.name == methodName }
        privateMethod?.isAccessible = true

        val argList = args.toMutableList()
        (argList as ArrayList).add(0, objectInstance)
        val argArr = argList.toArray()

        return privateMethod?.call(*argArr)
    }

    @Test
    fun `shipRefineMaterials`(){
        clear()
        val shipId = "test1"
        addShip(Shipbuilder(dateHelperMock)
            .setSymbol(shipId)
            .setCargo(
                capacity = 10,
                inventory = listOf(
                    Ship.Cargo.InventoryItem(
                        symbol = "IRON_ORE",
                        units = 5
                    )
                ),
            )
            .build()
        )
        fleetServiceMock.addRefineData(
            FleetServiceMock.RefineData(
                "IRON_ORE",
                2,
                "IRON",
                1
            )
        )


        val response = fleetServiceMock.shipRefineMaterials(
            shipId = shipId,
            produce = "IRON"
        )

        //test response consumed
        assertEquals("IRON_ORE", response.consumed.firstOrNull { it.tradeSymbol == "IRON_ORE" }?.tradeSymbol)
        assertEquals(2, response.consumed.firstOrNull { it.tradeSymbol == "IRON_ORE" }?.units)

        //test response produced
        assertEquals("IRON", response.produced.firstOrNull { it.tradeSymbol == "IRON" }?.tradeSymbol)
        assertEquals(1, response.produced.firstOrNull { it.tradeSymbol == "IRON" }?.units)

        //test response cargo
        val ironInventory = response.cargo.inventory.firstOrNull {it.symbol == "IRON"}
        assertEquals(1, ironInventory?.units)

        val ironOreInventory = response.cargo.inventory.firstOrNull {it.symbol == "IRON_ORE"}
        assertEquals(3, ironOreInventory?.units)

        val ship = fleetServiceMock.getShip(shipId)
        assertEquals(response.cargo, ship.cargo)
    }

    @Test
    fun `shipExtract`(){
        clear()
        val shipId = "test1"
        addShip(Shipbuilder(dateHelperMock)
            .setSymbol(shipId)
            .setCargo(
                capacity = 10,
                inventory = listOf(
                    Ship.Cargo.InventoryItem(
                        symbol = "IRON_ORE",
                        units = 5
                    )
                ),
            )
            .build()
        )
        fleetServiceMock.extractYield = ExtractResponse.Yield(
            symbol = "COPPER_ORE",
            units = 5
        )

        val response = fleetServiceMock.shipExtract(shipId)
        val copperOreInventory = response.cargo.inventory.firstOrNull {it.symbol == "COPPER_ORE"}
        assertEquals(5, copperOreInventory?.units)

        val ship = fleetServiceMock.getShip(shipId)
        assertEquals(response.cargo, ship.cargo)
    }

    @Test
    fun `shipNavigate`(){
        clear()
        val shipId = "test1"
        addShip(Shipbuilder(dateHelperMock)
            .setSymbol(shipId)
            .setNav(Ship.Nav(
                waypointSymbol = "startWaypoint",
                status = Ship.Nav.Statue.InOrbit
            ))
            .setFuel(
                Ship.Fuel(
                    current = 100,
                    capacity = 100
                ))
            .build()
        )

        val response = fleetServiceMock.shipNavigate(shipId, "destinationWaypoint")

        assertEquals("startWaypoint", response.nav.route.departure.symbol)
        assertEquals("destinationWaypoint", response.nav.route.destination.symbol)

        assertEquals(99, response.fuel.current)

        val ship = fleetServiceMock.getShip(shipId)
        assertEquals(response.nav, ship.nav)
        assertEquals(response.fuel, ship.fuel)
    }

    @Test
    fun `shipWarp`(){
        clear()
        val shipId = "test1"
        addShip(Shipbuilder(dateHelperMock)
            .setSymbol(shipId)
            .setNav(Ship.Nav(
                waypointSymbol = "startWaypoint",
                status = Ship.Nav.Statue.InOrbit
            ))
            .setFuel(
                Ship.Fuel(
                    current = 100,
                    capacity = 100
                ))
            .build()
        )

        val response = fleetServiceMock.shipWarp(shipId, "destinationWaypoint")

        assertEquals("startWaypoint", response.nav.route.departure.symbol)
        assertEquals("destinationWaypoint", response.nav.route.destination.symbol)

        assertEquals(99, response.fuel.current)

        val ship = fleetServiceMock.getShip(shipId)
        assertEquals(response.nav, ship.nav)
        assertEquals(response.fuel, ship.fuel)
    }

    @Test
    fun `shipJump`(){
        clear()
        val shipId = "test1"
        addShip(Shipbuilder(dateHelperMock)
            .setSymbol(shipId)
            .setNav(Ship.Nav(
                waypointSymbol = "startWaypoint",
                status = Ship.Nav.Statue.InOrbit
            ))
            .setFuel(
                Ship.Fuel(
                    current = 100,
                    capacity = 100
                ))
            .build()
        )

        val destinationSystem = "destinationSystem"
        val response = fleetServiceMock.shipJump(shipId, "destinationSystem")

        assertEquals("startWaypoint", response.nav.route.departure.symbol)
        assertEquals("$destinationSystem-1", response.nav.route.destination.symbol)
        assertEquals("$destinationSystem", response.nav.systemSymbol)

        val ship = fleetServiceMock.getShip(shipId)
        assertEquals(response.nav, ship.nav)
    }

    @Test
    fun `shipSellCargo`(){
        clear()
        val shipId = "test1"

        val pricePerUnit = 2
        fleetServiceMock.pricePerUnit = pricePerUnit

        addShip(Shipbuilder(dateHelperMock)
            .setSymbol(shipId)
            .setCargo(100, listOf(
                Ship.Cargo.InventoryItem(
                    symbol = "IRON_ORE",
                    units = 5
                ),
                Ship.Cargo.InventoryItem(
                    symbol = "IRON",
                    units = 5
                )
            ))
            .build()
        )

        val sellAmount = 2

        val response = fleetServiceMock.shipSellCargo(shipId, "IRON_ORE", sellAmount)

        assertEquals(8, response.cargo.units)
        assertEquals(3, response.cargo.inventory.firstOrNull{it.symbol == "IRON_ORE"}?.units)

        assertEquals(pricePerUnit, response.transaction.pricePerUnit)
        assertEquals(pricePerUnit*sellAmount, response.transaction.totalPrice)
        assertEquals(sellAmount, response.transaction.units)

        val ship = fleetServiceMock.getShip(shipId)
        assertEquals(response.cargo, ship.cargo)
    }

    @Test
    fun `shipPurchaseCargo`(){
        clear()
        val shipId = "test1"

        val pricePerUnit = 2
        fleetServiceMock.pricePerUnit = pricePerUnit

        addShip(Shipbuilder(dateHelperMock)
            .setSymbol(shipId)
            .setCargo(100, listOf(
                Ship.Cargo.InventoryItem(
                    symbol = "IRON",
                    units = 5
                )
            ))
            .build()
        )

        val purchaseAmount = 2

        val response = fleetServiceMock.shipPurchaseCargo(shipId, "IRON_ORE", purchaseAmount)

        assertEquals(7, response.cargo.units)
        assertEquals(2, response.cargo.inventory.firstOrNull{it.symbol == "IRON_ORE"}?.units)

        assertEquals(pricePerUnit, response.transaction.pricePerUnit)
        assertEquals(pricePerUnit*purchaseAmount, response.transaction.totalPrice)
        assertEquals(purchaseAmount, response.transaction.units)

        val ship = fleetServiceMock.getShip(shipId)
        assertEquals(response.cargo, ship.cargo)
    }

    @Test
    fun `shipJettisonCargo`() {
        clear()
        val shipId = "test1"

        addShip(Shipbuilder(dateHelperMock)
            .setSymbol(shipId)
            .setCargo(100, listOf(
                Ship.Cargo.InventoryItem(
                    symbol = "IRON",
                    units = 5
                )
            ))
            .build()
        )

        val jettisonAmount = 2

        val response = fleetServiceMock.shipJettisonCargo(shipId, "IRON", jettisonAmount)

        assertEquals(3, response.cargo.units)
        assertEquals(3, response.cargo.inventory.firstOrNull{it.symbol == "IRON"}?.units)

        val ship = fleetServiceMock.getShip(shipId)
        assertEquals(response.cargo, ship.cargo)
    }

    @Test
    fun `shipTransferCargo`(){
        clear()
        val shipId1 = "test1"
        addShip(Shipbuilder(dateHelperMock)
            .setSymbol(shipId1)
            .setCargo(100, listOf(
                Ship.Cargo.InventoryItem(
                    symbol = "IRON",
                    units = 5
                )
            ))
            .build()
        )

        val shipId2 = "test2"
        addShip(Shipbuilder(dateHelperMock)
            .setSymbol(shipId2)
            .setCargo(100, listOf(
                Ship.Cargo.InventoryItem(
                    symbol = "IRON",
                    units = 5
                )
            ))
            .build()
        )

        val transferAmount = 2

        val response = fleetServiceMock.shipTransferCargo(shipId1, "IRON", transferAmount, shipId2)

        assertEquals(3, response.units)
        assertEquals(3, response.inventory.firstOrNull{it.symbol == "IRON"}?.units)

        val ship1 = fleetServiceMock.getShip(shipId1)
        assertEquals(response, ship1.cargo)

        val ship2 = fleetServiceMock.getShip(shipId2)
        assertEquals(7, ship2.cargo.units)
        assertEquals(7, ship2.cargo.inventory.firstOrNull{it.symbol == "IRON"}?.units)
    }
}