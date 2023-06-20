package com.jaehl.spaceTraders.data.controllers

import androidx.compose.ui.test.junit4.createComposeRule
import com.jaehl.spaceTraders.data.model.*
import com.jaehl.spaceTraders.data.model.response.Cooldown
import com.jaehl.spaceTraders.data.model.response.ExtractResponse
import com.jaehl.spaceTraders.data.services.mock.ContractServiceMock
import com.jaehl.spaceTraders.data.services.mock.FleetServiceMock
import com.jaehl.spaceTraders.data.services.mock.Shipbuilder
import com.jaehl.spaceTraders.ui.pages.runTask.tasks.BasicTaskInterface
import com.jaehl.spaceTraders.ui.pages.runTask.tasks.MinerTaskInterface
import com.jaehl.spaceTraders.ui.pages.runTask.tasks.RefineItem
import com.jaehl.spaceTraders.util.DateHelperMock
import org.junit.Rule
import org.junit.Test
import java.util.Date
import kotlin.test.assertEquals

class ShipControllerTest {

    @get:Rule
    val compose = createComposeRule()

    private var agent = Agent()
    private val shipMap = HashMap<String, Ship>()
    private val transActions = ArrayList<Transaction>()
    private val shipCoolDowns = HashMap<String, Cooldown?>()
    private val refineDataMap = HashMap<String, FleetServiceMock.RefineData>()
    private val contractMap = HashMap<String, Contract>()

    private val dateHelperMock = DateHelperMock()

    private val fleetServiceMock = FleetServiceMock(
        agent = agent,
        shipMap = shipMap,
        transActions = transActions,
        shipCoolDowns = shipCoolDowns,
        refineDataMap = refineDataMap,
        dateHelper = dateHelperMock
    )

    private val contractServiceMock = ContractServiceMock(
        agent = agent,
        shipMap = shipMap,
        contractMap = contractMap
    )

    private fun createShipControllerImp(ship : Ship) : ShipControllerImp {
        return ShipControllerImp(
            ship,
            fleetServiceMock,
            contractServiceMock,
            dateHelperMock
        )
    }

    private fun clear() {
        agent = Agent()
        shipMap.clear()
        shipCoolDowns.clear()
        refineDataMap.clear()
        contractMap.clear()
    }

    private fun addShip(ship : Ship) : Ship{
        shipMap[ship.symbol] = ship
        return ship
    }

    fun addRefineData(refineData : FleetServiceMock.RefineData) {
        refineDataMap[refineData.outputCargoId] = refineData
    }


    @Test
    fun `refine`(){
        clear()

        val shipId1 = "test1"
        val ship = addShip(
            Shipbuilder(dateHelperMock)
            .setSymbol(shipId1)
            .setCargo(
                capacity = 10,
                inventory = listOf(
                    Ship.Cargo.InventoryItem(
                        symbol = "IRON_ORE",
                        units = 5
                    ),
                    Ship.Cargo.InventoryItem(
                        symbol = "COPPER_ORE",
                        units = 5
                    )
                ),
            )
            .build()
        )

        addRefineData(
            FleetServiceMock.RefineData(
                "IRON_ORE",
                2,
                "IRON",
                1
            )
        )

        val shipController = createShipControllerImp(ship)
        shipController.refine("IRON")

        assertEquals(9, shipController.getShip().cargo.units )
        val ironOreInventory = shipController.getShip().cargo.inventory.firstOrNull {it.symbol == "IRON_ORE"}
        assertEquals(3, ironOreInventory?.units)

        val ironInventory = shipController.getShip().cargo.inventory.firstOrNull {it.symbol == "IRON"}
        assertEquals(1, ironInventory?.units)

    }

    @Test
    fun `extract`() {
        clear()

        val shipId1 = "test1"
        val ship = addShip(
            Shipbuilder(dateHelperMock)
                .setSymbol(shipId1)
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

        val shipController = createShipControllerImp(ship)
        shipController.extract(null)

        assertEquals(10, shipController.getShip().cargo.units )
        val copperOreInventory = shipController.getShip().cargo.inventory.firstOrNull {it.symbol == "COPPER_ORE"}
        assertEquals(5, copperOreInventory?.units)
    }

    @Test
    fun `sell`() {
        clear()

        val shipId1 = "test1"
        val ship = addShip(
            Shipbuilder(dateHelperMock)
                .setSymbol(shipId1)
                .setCargo(
                    capacity = 10,
                    inventory = listOf(
                        Ship.Cargo.InventoryItem(
                            symbol = "COPPER_ORE",
                            units = 5
                        )
                    ),
                )
                .build()
        )

        fleetServiceMock.pricePerUnit = 2

        val shipController = createShipControllerImp(ship)
        val transaction = shipController.sell("COPPER_ORE", 4)

        assertEquals(1, shipController.getShip().cargo.units )
        val copperOreInventory = shipController.getShip().cargo.inventory.firstOrNull {it.symbol == "COPPER_ORE"}
        assertEquals(1, copperOreInventory?.units)

        assertEquals(8, transaction?.totalPrice)
        assertEquals(4, transaction?.units)
        assertEquals(Transaction.Type.Sell, transaction?.type)
    }

    @Test
    fun `sell missing error`() {
        clear()

        val shipId1 = "test1"
        val ship = addShip(
            Shipbuilder(dateHelperMock)
                .setSymbol(shipId1)
                .setCargo(
                    capacity = 10,
                    inventory = listOf(
                        Ship.Cargo.InventoryItem(
                            symbol = "COPPER_ORE",
                            units = 5
                        )
                    )
                )
                .build()
        )

        fleetServiceMock.pricePerUnit = 2

        val shipController = createShipControllerImp(ship)
        val transaction = shipController.sell("IRON_ORE", 4)

        assertEquals(5, shipController.getShip().cargo.units )
        assertEquals(null, transaction)
    }

    @Test
    fun `sellAll`() {
        clear()

        val shipId1 = "test1"
        val ship = addShip(
            Shipbuilder(dateHelperMock)
                .setSymbol(shipId1)
                .setCargo(
                    capacity = 10,
                    inventory = listOf(
                        Ship.Cargo.InventoryItem(
                            symbol = "COPPER_ORE",
                            units = 5
                        ),
                        Ship.Cargo.InventoryItem(
                            symbol = "IRON_ORE",
                            units = 5
                        )
                    )
                )
                .build()
        )

        fleetServiceMock.pricePerUnit = 2

        val shipController = createShipControllerImp(ship)
        val transaction = shipController.sellAll("COPPER_ORE")

        assertEquals(5, shipController.getShip().cargo.units )
        assertEquals(5, transaction?.units)
    }

    @Test
    fun `sellAll List`() {
        clear()

        val shipId1 = "test1"
        val ship = addShip(
            Shipbuilder(dateHelperMock)
                .setSymbol(shipId1)
                .setCargo(
                    capacity = 20,
                    inventory = listOf(
                        Ship.Cargo.InventoryItem(
                            symbol = "COPPER_ORE",
                            units = 7
                        ),
                        Ship.Cargo.InventoryItem(
                            symbol = "IRON_ORE",
                            units = 5
                        ),
                        Ship.Cargo.InventoryItem(
                            symbol = "ALUMINUM_ORE",
                            units = 4
                        )
                    )
                )
                .build()
        )

        fleetServiceMock.pricePerUnit = 2

        val sellList = listOf(
            "ALUMINUM_ORE",
            "COPPER_ORE"
        )

        val shipController = createShipControllerImp(ship)
        val transactions = shipController.sellAll(sellList)

        assertEquals(5, shipController.getShip().cargo.units )
        val inventoryItem = shipController.getShip().cargo.inventory.firstOrNull()
        assertEquals("IRON_ORE", inventoryItem?.symbol)

        assertEquals(2, transactions.size)
        assertEquals("ALUMINUM_ORE", transactions[0].tradeSymbol)
        assertEquals(4, transactions[0].units)
        assertEquals("COPPER_ORE", transactions[1].tradeSymbol)
        assertEquals(7, transactions[1].units)
    }

    @Test
    fun `purchase`() {
        clear()

        val shipId1 = "test1"
        val ship = addShip(
            Shipbuilder(dateHelperMock)
                .setSymbol(shipId1)
                .setCargo(
                    capacity = 10,
                    inventory = listOf(
                        Ship.Cargo.InventoryItem(
                            symbol = "COPPER_ORE",
                            units = 5
                        )
                    ),
                )
                .build()
        )

        fleetServiceMock.pricePerUnit = 2

        val shipController = createShipControllerImp(ship)
        val transaction = shipController.purchase("COPPER_ORE", 4)

        assertEquals(9, shipController.getShip().cargo.units )
        val copperOreInventory = shipController.getShip().cargo.inventory.firstOrNull {it.symbol == "COPPER_ORE"}
        assertEquals(9, copperOreInventory?.units)

        assertEquals(8, transaction.totalPrice)
        assertEquals(4, transaction.units)
        assertEquals(Transaction.Type.Purchase, transaction.type)
    }

    @Test
    fun `contractDeliver`() {
        clear()

        val shipId1 = "test1"
        val ship = addShip(
            Shipbuilder(dateHelperMock)
                .setSymbol(shipId1)
                .setCargo(
                    capacity = 10,
                    inventory = listOf(
                        Ship.Cargo.InventoryItem(
                            symbol = "COPPER_ORE",
                            units = 5
                        )
                    ),
                )
                .build()
        )

        val contractId = "contractId1"
        contractMap[contractId] = Contract(
            id = contractId,
            terms = Contract.Terms(
                deliver = listOf(
                    Contract.Terms.Deliver(
                        tradeSymbol = "COPPER_ORE",
                        destinationSymbol = "destinationSymbol",
                        unitsRequired = 500,
                        unitsFulfilled = 0
                    )
                )
            )
        )

        val shipController = createShipControllerImp(ship)
        shipController.contractDeliver(contractId, "COPPER_ORE", 4)

        assertEquals(1, shipController.getShip().cargo.units )
        val copperOreInventory = shipController.getShip().cargo.inventory.firstOrNull {it.symbol == "COPPER_ORE"}
        assertEquals(1, copperOreInventory?.units)
    }

    @Test
    fun `dock`(){
        clear()

        val shipId1 = "test1"
        val ship = addShip(
            Shipbuilder(dateHelperMock)
                .setSymbol(shipId1)
                .setNavStatus(Ship.Nav.Statue.InOrbit)
                .build()
        )

        val shipController = createShipControllerImp(ship)
        shipController.dock()
        assertEquals(Ship.Nav.Statue.Docked, shipController.getShip().nav.status)

    }

    @Test
    fun `enterOrbit`(){
        clear()

        val shipId1 = "test1"
        val ship = addShip(
            Shipbuilder(dateHelperMock)
                .setSymbol(shipId1)
                .setNavStatus(Ship.Nav.Statue.Docked)
                .build()
        )

        val shipController = createShipControllerImp(ship)
        shipController.enterOrbit()
        assertEquals(Ship.Nav.Statue.InOrbit, shipController.getShip().nav.status)

    }

    @Test
    fun `refuel`(){
        clear()

        val shipId1 = "test1"
        val ship = addShip(
            Shipbuilder(dateHelperMock)
                .setSymbol(shipId1)
                .setFuel(
                    Ship.Fuel(
                    capacity = 100,
                    current = 50
                ))
                .setNavStatus(Ship.Nav.Statue.Docked)
                .build()
        )

        val shipController = createShipControllerImp(ship)
        shipController.refuel()
        assertEquals(100, shipController.getShip().fuel.current)

    }

    @Test
    fun `jettison`() {
        clear()

        val shipId1 = "test1"
        val ship = addShip(
            Shipbuilder(dateHelperMock)
                .setSymbol(shipId1)
                .setNavStatus(Ship.Nav.Statue.InOrbit)
                .setCargo(
                    capacity = 10,
                    inventory = listOf(
                        Ship.Cargo.InventoryItem(
                            symbol = "COPPER_ORE",
                            units = 5
                        )
                    ),
                )
                .build()
        )

        val shipController = createShipControllerImp(ship)
        shipController.jettison("COPPER_ORE", 4)

        assertEquals(1, shipController.getShip().cargo.units )
        val copperOreInventory = shipController.getShip().cargo.inventory.firstOrNull {it.symbol == "COPPER_ORE"}
        assertEquals(1, copperOreInventory?.units)
    }

    @Test
    fun `jettisonAllOfSingle`() {
        clear()

        val shipId1 = "test1"
        val ship = addShip(
            Shipbuilder(dateHelperMock)
                .setSymbol(shipId1)
                .setNavStatus(Ship.Nav.Statue.InOrbit)
                .setCargo(
                    capacity = 20,
                    inventory = listOf(
                        Ship.Cargo.InventoryItem(
                            symbol = "COPPER_ORE",
                            units = 7
                        ),
                        Ship.Cargo.InventoryItem(
                            symbol = "IRON_ORE",
                            units = 5
                        ),
                        Ship.Cargo.InventoryItem(
                            symbol = "ALUMINUM_ORE",
                            units = 4
                        )
                    ),
                )
                .build()
        )

        val shipController = createShipControllerImp(ship)
        shipController.jettisonAllOfSingle("IRON_ORE")

        assertEquals(11, shipController.getShip().cargo.units )
    }

    @Test
    fun `jettisonAllBut`() {
        clear()

        val shipId1 = "test1"
        val ship = addShip(
            Shipbuilder(dateHelperMock)
                .setSymbol(shipId1)
                .setNavStatus(Ship.Nav.Statue.InOrbit)
                .setCargo(
                    capacity = 20,
                    inventory = listOf(
                        Ship.Cargo.InventoryItem(
                            symbol = "COPPER_ORE",
                            units = 7
                        ),
                        Ship.Cargo.InventoryItem(
                            symbol = "IRON_ORE",
                            units = 5
                        ),
                        Ship.Cargo.InventoryItem(
                            symbol = "ALUMINUM_ORE",
                            units = 4
                        ),
                        Ship.Cargo.InventoryItem(
                            symbol = "ANTIMATTER",
                            units = 5
                        )
                    ),
                )
                .build()
        )

        val keepList = listOf(
            "COPPER_ORE",
            "ALUMINUM_ORE"
        )

        val shipController = createShipControllerImp(ship)
        shipController.jettisonAllBut(keepList)

        assertEquals(11, shipController.getShip().cargo.units )
        val copperOre = shipController.getShip().cargo.inventory.firstOrNull {it.symbol == "COPPER_ORE"}
        assertEquals(7, copperOre?.units)

        val aluminumOre = shipController.getShip().cargo.inventory.firstOrNull {it.symbol == "ALUMINUM_ORE"}
        assertEquals(4, aluminumOre?.units)
    }

    @Test
    fun `addCargo`() {
        clear()

        val shipId1 = "test1"
        val ship = addShip(
            Shipbuilder(dateHelperMock)
                .setSymbol(shipId1)
                .setNavStatus(Ship.Nav.Statue.InOrbit)
                .setCargo(
                    capacity = 10,
                    inventory = listOf(
                        Ship.Cargo.InventoryItem(
                            symbol = "COPPER_ORE",
                            units = 5
                        )
                    ),
                )
                .build()
        )

        val shipController = createShipControllerImp(ship)
        shipController.addCargo("COPPER_ORE", 4)

        assertEquals(9, shipController.getShip().cargo.units )
        val copperOreInventory = shipController.getShip().cargo.inventory.firstOrNull {it.symbol == "COPPER_ORE"}
        assertEquals(9, copperOreInventory?.units)
    }

    @Test
    fun `transferCargo`() {
        clear()

        val ship1 = addShip(
            Shipbuilder(dateHelperMock)
                .setSymbol("test1")
                .setNavStatus(Ship.Nav.Statue.InOrbit)
                .setCargo(
                    capacity = 10,
                    inventory = listOf(
                        Ship.Cargo.InventoryItem(
                            symbol = "COPPER_ORE",
                            units = 5
                        )
                    ),
                )
                .build()
        )
        val ship1Controller = createShipControllerImp(ship1)

        val ship2 = addShip(
            Shipbuilder(dateHelperMock)
                .setSymbol("test2")
                .setNavStatus(Ship.Nav.Statue.InOrbit)
                .setCargo(
                    capacity = 10,
                    inventory = listOf(
                        Ship.Cargo.InventoryItem(
                            symbol = "COPPER_ORE",
                            units = 5
                        )
                    ),
                )
                .build()
        )
        val ship2Controller = createShipControllerImp(ship2)


        ship1Controller.transferCargo(ship2Controller, "COPPER_ORE", 4)

        assertEquals(1, ship1Controller.getShip().cargo.units )
        val ship1CopperOreInventory = ship1Controller.getShip().cargo.inventory.firstOrNull {it.symbol == "COPPER_ORE"}
        assertEquals(1, ship1CopperOreInventory?.units)

        assertEquals(9, ship2Controller.getShip().cargo.units )
        val ship2CopperOreInventory = ship2Controller.getShip().cargo.inventory.firstOrNull {it.symbol == "COPPER_ORE"}
        assertEquals(9, ship2CopperOreInventory?.units)
    }

    @Test
    fun `transferCargo error full`() {
        clear()

        val ship1 = addShip(
            Shipbuilder(dateHelperMock)
                .setSymbol("test1")
                .setNavStatus(Ship.Nav.Statue.InOrbit)
                .setCargo(
                    capacity = 10,
                    inventory = listOf(
                        Ship.Cargo.InventoryItem(
                            symbol = "COPPER_ORE",
                            units = 10
                        )
                    ),
                )
                .build()
        )
        val ship1Controller = createShipControllerImp(ship1)

        val ship2 = addShip(
            Shipbuilder(dateHelperMock)
                .setSymbol("test2")
                .setNavStatus(Ship.Nav.Statue.InOrbit)
                .setCargo(
                    capacity = 20,
                    inventory = listOf(
                        Ship.Cargo.InventoryItem(
                            symbol = "COPPER_ORE",
                            units = 5
                        )
                    ),
                )
                .build()
        )
        val ship2Controller = createShipControllerImp(ship2)


        ship1Controller.transferCargo(ship2Controller, "COPPER_ORE", 7)

        assertEquals(3, ship1Controller.getShip().cargo.units )
        val ship1CopperOreInventory = ship1Controller.getShip().cargo.inventory.firstOrNull {it.symbol == "COPPER_ORE"}
        assertEquals(3, ship1CopperOreInventory?.units)

        assertEquals(12, ship2Controller.getShip().cargo.units )
        val ship2CopperOreInventory = ship2Controller.getShip().cargo.inventory.firstOrNull {it.symbol == "COPPER_ORE"}
        assertEquals(12, ship2CopperOreInventory?.units)
    }

    @Test
    fun `transferAllCargo`() {
        clear()

        val ship1 = addShip(
            Shipbuilder(dateHelperMock)
                .setSymbol("test1")
                .setNavStatus(Ship.Nav.Statue.InOrbit)
                .setCargo(
                    capacity = 10,
                    inventory = listOf(
                        Ship.Cargo.InventoryItem(
                            symbol = "COPPER_ORE",
                            units = 5
                        )
                    ),
                )
                .build()
        )
        val ship1Controller = createShipControllerImp(ship1)

        val ship2 = addShip(
            Shipbuilder(dateHelperMock)
                .setSymbol("test2")
                .setNavStatus(Ship.Nav.Statue.InOrbit)
                .setCargo(
                    capacity = 10,
                    inventory = listOf(
                        Ship.Cargo.InventoryItem(
                            symbol = "COPPER_ORE",
                            units = 5
                        )
                    ),
                )
                .build()
        )
        val ship2Controller = createShipControllerImp(ship2)


        ship1Controller.transferAllCargo(ship2Controller, "COPPER_ORE")

        assertEquals(0, ship1Controller.getShip().cargo.units )

        assertEquals(10, ship2Controller.getShip().cargo.units )
        val ship2CopperOreInventory = ship2Controller.getShip().cargo.inventory.firstOrNull {it.symbol == "COPPER_ORE"}
        assertEquals(10, ship2CopperOreInventory?.units)
    }

    @Test
    fun `transferAllCargo list`() {
        clear()

        val ship1 = addShip(
            Shipbuilder(dateHelperMock)
                .setSymbol("test1")
                .setNavStatus(Ship.Nav.Statue.InOrbit)
                .setCargo(
                    capacity = 20,
                    inventory = listOf(
                        Ship.Cargo.InventoryItem(
                            symbol = "COPPER_ORE",
                            units = 7
                        ),
                        Ship.Cargo.InventoryItem(
                            symbol = "IRON_ORE",
                            units = 5
                        ),
                        Ship.Cargo.InventoryItem(
                            symbol = "ALUMINUM_ORE",
                            units = 4
                        ),
                        Ship.Cargo.InventoryItem(
                            symbol = "ANTIMATTER",
                            units = 5
                        )
                    ),
                )
                .build()
        )
        val ship1Controller = createShipControllerImp(ship1)

        val ship2 = addShip(
            Shipbuilder(dateHelperMock)
                .setSymbol("test2")
                .setNavStatus(Ship.Nav.Statue.InOrbit)
                .setCargo(
                    capacity = 100,
                    inventory = listOf(
                        Ship.Cargo.InventoryItem(
                            symbol = "COPPER_ORE",
                            units = 5
                        )
                    ),
                )
                .build()
        )
        val ship2Controller = createShipControllerImp(ship2)

        val transferList = listOf(
            "IRON_ORE",
            "ANTIMATTER"
        )
        ship1Controller.transferAllCargo(ship2Controller, transferList)

        assertEquals(11, ship1Controller.getShip().cargo.units )
        assertEquals(7, ship1Controller.getShip().cargo.inventory.firstOrNull {it.symbol == "COPPER_ORE"}?.units)
        assertEquals(4, ship1Controller.getShip().cargo.inventory.firstOrNull {it.symbol == "ALUMINUM_ORE"}?.units)

        assertEquals(15, ship2Controller.getShip().cargo.units )
        assertEquals(5, ship2Controller.getShip().cargo.inventory.firstOrNull {it.symbol == "IRON_ORE"}?.units)
        assertEquals(5, ship2Controller.getShip().cargo.inventory.firstOrNull {it.symbol == "ANTIMATTER"}?.units)
    }

    @Test
    fun `navigate`() {
        clear()

        val ship = addShip(
            Shipbuilder(dateHelperMock)
                .setSymbol("test1")
                .setNav(
                    Ship.Nav(
                        systemSymbol ="systemSymbol",
                        waypointSymbol = "startWaypoint",
                        status = Ship.Nav.Statue.InOrbit
                    )
                )
                .build()
        )

        val shipController = createShipControllerImp(ship)
        shipController.navigate("destinationWaypoint")

        assertEquals("destinationWaypoint", shipController.getShip().nav.waypointSymbol)
        //assertEquals(Date(), shipController.getShip().nav.route.arrival)

    }
}