package com.jaehl.spaceTraders.ui.pages.runTask.agents

import com.jaehl.spaceTraders.data.controllers.ShipController
import com.jaehl.spaceTraders.data.controllers.ShipControllerImp
import com.jaehl.spaceTraders.data.model.*
import com.jaehl.spaceTraders.data.model.response.Cooldown
import com.jaehl.spaceTraders.data.model.response.ExtractResponse
import com.jaehl.spaceTraders.data.repo.mock.MarketRepoMock
import com.jaehl.spaceTraders.data.services.mock.ContractServiceMock
import com.jaehl.spaceTraders.data.services.mock.FleetServiceMock
import com.jaehl.spaceTraders.data.services.mock.Shipbuilder
import com.jaehl.spaceTraders.ui.pages.runTask.tasks.BasicTaskInterface
import com.jaehl.spaceTraders.ui.pages.runTask.tasks.MinerTaskInterface
import com.jaehl.spaceTraders.ui.pages.runTask.tasks.RefineItem
import com.jaehl.spaceTraders.util.DateHelperMock
import com.jaehl.spaceTraders.util.Logger
import com.jaehl.spaceTraders.util.LoggerImp
import java.util.*
import kotlin.collections.HashMap
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StaticMinerAgentTest {

    private var agent = Agent()
    private val shipMap = HashMap<String, Ship>()
    private val transActions = ArrayList<Transaction>()
    private val shipCoolDowns = HashMap<String, Cooldown?>()
    private val refineDataMap = HashMap<String, FleetServiceMock.RefineData>()
    private val contractMap = HashMap<String, Contract>()

    private val dateHelperMock = DateHelperMock()

    private val marketRepoMock = MarketRepoMock(dateHelperMock)

    private val logger = LoggerImp()

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

    private fun createStaticMinerAgent(ship : Ship) : StaticMinerAgent {
        return StaticMinerAgent(
            shipController = ShipControllerImp(
                ship = ship,
                fleetService = fleetServiceMock,
                contractService = contractServiceMock,
                dateHelper = dateHelperMock
            ),
            dateHelper = dateHelperMock,
            marketRepo = marketRepoMock,
            logger = logger
        )
    }

    private fun clear() {
        agent = Agent()
        shipMap.clear()
        shipCoolDowns.clear()
        refineDataMap.clear()
        contractMap.clear()
        marketRepoMock.clear()
    }

    private fun addShip(ship : Ship) : Ship{
        shipMap[ship.symbol] = ship
        return ship
    }

    private fun createAndAddMarketHistory(waypointId : String, tradeGoods : List<TradeGood>) {
        marketRepoMock.addMarketHistory(
            MarketHistory(
                symbol = waypointId,
                detailedInfo = true,
                tradeGoods = tradeGoods
            )
        )
    }

    data class TaskDataMock(
        val useSurvey: Boolean = false,
        val miningSurvey : MiningSurvey = MiningSurvey(),
        val refineItemList : List<RefineItem> = listOf(),
        //val availableTransport : ShipController? = null,
        //val availableRefineShip : ShipController? = null,
        //val localSell : List<String> = listOf(),
        //val localKeep : List<String> = listOf(),
        val asteroidWaypoint : String = "asteroidWaypoint",
        val cargoShipController : ShipController? = null,
        val cargoShipAcceptableCargoList : List<String> = listOf()

    ) : MinerTaskInterface, BasicTaskInterface {
        override fun retrieveUseSurvey(): Boolean = useSurvey
        override fun retrieveMiningSurvey(): MiningSurvey = miningSurvey
        override fun retrieveRefineItemList(): List<RefineItem> = refineItemList
//        override fun retrieveAvailableTransport(locationWaypoint: String): ShipController? = availableTransport
//        override fun retrieveAvailableRefineShip(locationWaypoint: String): ShipController? = availableRefineShip
//        override fun retrieveLocalSell(locationWaypoint: String): List<String> = localSell
//        override fun retrieveLocalKeep(locationWaypoint: String): List<String> = localKeep
        override fun retrieveAsteroidWaypoint(): String = asteroidWaypoint
        override fun findShipForCargo(
            shipId: String,
            systemId: String,
            waypointId: String,
            cargoId: String
        ): ShipController? {
            if(cargoShipAcceptableCargoList.contains(cargoId)) {
                return cargoShipController
            }
            return null
        }
    }

    @Test
    fun `setup start at asteroidWaypoint`() {
        clear()

        val taskDataMock = TaskDataMock(
            useSurvey = true,
            miningSurvey = MiningSurvey(expiration = Date(0)),
            asteroidWaypoint = "asteroidWaypoint"
        )

        val shipId1 = "test1"
        val agent = createStaticMinerAgent(
            ship = addShip(
                Shipbuilder(dateHelperMock)
                    .setSymbol(shipId1)
                    .setNav(
                        Ship.Nav(
                            waypointSymbol = taskDataMock.asteroidWaypoint,
                            status = Ship.Nav.Statue.Docked,
                    ))
                    .build()
            )
        )


        agent.setup(taskDataMock)
        assertEquals(Ship.Nav.Statue.InOrbit, agent.getShip().nav.status)
    }

    @Test
    fun `setup start not at asteroidWaypoint`() {
        clear()

        val taskDataMock = TaskDataMock(
            useSurvey = true,
            miningSurvey = MiningSurvey(expiration = Date(0)),
            asteroidWaypoint = "asteroidWaypoint"
        )

        val shipId1 = "test1"
        val agent = createStaticMinerAgent(
            ship = addShip(
                Shipbuilder(dateHelperMock)
                    .setSymbol(shipId1)
                    .setNav(
                        Ship.Nav(
                            waypointSymbol = "someOtherPlace",
                            status = Ship.Nav.Statue.Docked
                        ))
                    .build()
            )
        )

        agent.setup(taskDataMock)
        assertEquals(Ship.Nav.Statue.InTransit, agent.getShip().nav.status)
        assertEquals(taskDataMock.asteroidWaypoint, agent.getShip().nav.waypointSymbol)
    }

    @Test
    fun `run MiningSurvey Expired`() {
        clear()

        val shipId1 = "test1"
        val agent = createStaticMinerAgent(
            ship = addShip(
                Shipbuilder(dateHelperMock)
                    .setSymbol(shipId1)
                    .setNavStatus(Ship.Nav.Statue.InOrbit)
                    .build()
            )
        )
        val taskDataMock = TaskDataMock(
            useSurvey = true,
            miningSurvey = MiningSurvey(expiration = Date(0))
        )

        agent.run(taskDataMock)
        assertEquals(0L, agent.getShipController().getCoolDown().expiration.time)
    }

    @Test
    fun `run MiningSurvey empty cargo`() {
        clear()

        val shipId1 = "test1"
        val agent = createStaticMinerAgent(
            ship = addShip(
                Shipbuilder(dateHelperMock)
                    .setSymbol(shipId1)
                    .setNavStatus(Ship.Nav.Statue.InOrbit)
                    .setCargo(
                        capacity = 10,
                        inventory = listOf()
                    )
                    .build()
            )
        )
        val taskDataMock = TaskDataMock(
            useSurvey = false,
            miningSurvey = MiningSurvey(expiration = Date(0))
        )

        fleetServiceMock.extractYield = ExtractResponse.Yield(
            symbol = "COPPER_ORE",
            units = 10
        )

        agent.run(taskDataMock)
        assertEquals(10, agent.getShip().cargo.units)
    }

    @Test
    fun `run MiningSurvey Full cargo with transport Ship`() {
        clear()

        val systemId = "systemId"
        val asteroidFieldId = "asteroidFieldId"

        val shipId1 = "test1"
        val agent = createStaticMinerAgent(
            ship = addShip(
                Shipbuilder(dateHelperMock)
                    .setSymbol(shipId1)
                    .setNav(
                        Ship.Nav(
                            status = Ship.Nav.Statue.InOrbit,
                            waypointSymbol = asteroidFieldId,
                            systemSymbol = systemId,
                            route = Ship.Nav.NavRoute(
                                arrival = dateHelperMock.getNow()
                            )
                        )
                    )
                    .setCargo(
                        capacity = 10,
                        inventory = listOf(
                            Ship.Cargo.InventoryItem(
                                symbol = "IRON_ORE",
                                units = 10
                            )
                        )
                    )
                    .build()
            )
        )

        val transportShipController = ShipControllerImp(
            ship = addShip(
                Shipbuilder(dateHelperMock)
                    .setSymbol("transportShip")
                    .setNav(
                        Ship.Nav(
                            status = Ship.Nav.Statue.InOrbit,
                            waypointSymbol = asteroidFieldId,
                            systemSymbol = systemId,
                            route = Ship.Nav.NavRoute(
                                arrival = dateHelperMock.getNow()
                            )
                        )
                    )
                    .setCargo(
                        capacity = 20,
                        inventory = listOf()
                    )
                    .build()
            ),
            fleetService = fleetServiceMock,
            contractService = contractServiceMock,
            dateHelper = dateHelperMock
        )

        val taskDataMock = TaskDataMock(
            useSurvey = false,
            miningSurvey = MiningSurvey(expiration = Date(0)),
            cargoShipController = transportShipController,
            cargoShipAcceptableCargoList = listOf("IRON_ORE")
        )

        createAndAddMarketHistory(
            waypointId = asteroidFieldId,
            tradeGoods = listOf(
                TradeGood(
                    symbol = "IRON_ORE",
                    sellPrice = 2
                )
            )
        )

        fleetServiceMock.extractYield = ExtractResponse.Yield(
            symbol = "COPPER_ORE",
            units = 5
        )

        agent.run(taskDataMock)
        assertEquals(5, agent.getShip().cargo.units)
        assertEquals(5, agent.getShip().cargo.inventory.firstOrNull{it.symbol == "COPPER_ORE"}?.units)

        assertEquals(10, transportShipController.getShip().cargo.units)
        assertEquals(10, transportShipController.getShip().cargo.inventory.firstOrNull{it.symbol == "IRON_ORE"}?.units)
    }

    @Test
    fun `run MiningSurvey Full cargo with local sell`() {
        clear()

        val systemId = "systemId"
        val asteroidFieldId = "asteroidFieldId"

        val shipId1 = "test1"
        val agent = createStaticMinerAgent(
            ship = addShip(
                Shipbuilder(dateHelperMock)
                    .setSymbol(shipId1)
                    .setNav(
                        Ship.Nav(
                            status = Ship.Nav.Statue.InOrbit,
                            waypointSymbol = asteroidFieldId,
                            systemSymbol = systemId,
                            route = Ship.Nav.NavRoute(
                                arrival = dateHelperMock.getNow()
                            )
                        )
                    )
                    .setCargo(
                        capacity = 10,
                        inventory = listOf(
                            Ship.Cargo.InventoryItem(
                                symbol = "IRON_ORE",
                                units = 10
                            )
                        )
                    )
                    .build()
            )
        )

        val taskDataMock = TaskDataMock(
            useSurvey = false,
            miningSurvey = MiningSurvey(expiration = Date(0)),
            cargoShipController = null
        )

        createAndAddMarketHistory(
            waypointId = asteroidFieldId,
            tradeGoods = listOf(
                TradeGood(
                    symbol = "IRON_ORE",
                    sellPrice = 2
                )
            )
        )

        fleetServiceMock.extractYield = ExtractResponse.Yield(
            symbol = "COPPER_ORE",
            units = 5
        )

        agent.run(taskDataMock)
        assertEquals(5, agent.getShip().cargo.units)
        assertEquals(5, agent.getShip().cargo.inventory.firstOrNull{it.symbol == "COPPER_ORE"}?.units)

        val transAction = transActions.firstOrNull()
        assertEquals(10, transAction?.units)
        assertEquals(Transaction.Type.Sell, transAction?.type)
        assertEquals("IRON_ORE", transAction?.tradeSymbol)



    }

    @Test
    fun `run MiningSurvey Full cargo jettison all`() {
        clear()

        val shipId1 = "test1"
        val agent = createStaticMinerAgent(
            ship = addShip(
                Shipbuilder(dateHelperMock)
                    .setSymbol(shipId1)
                    .setNavStatus(Ship.Nav.Statue.InOrbit)
                    .setCargo(
                        capacity = 10,
                        inventory = listOf(
                            Ship.Cargo.InventoryItem(
                                symbol = "IRON_ORE",
                                units = 10
                            )
                        )
                    )
                    .build()
            )
        )

        val taskDataMock = TaskDataMock(
            useSurvey = false,
            miningSurvey = MiningSurvey(expiration = Date(0)),
            cargoShipController = null
        )

        fleetServiceMock.extractYield = ExtractResponse.Yield(
            symbol = "COPPER_ORE",
            units = 5
        )

        agent.run(taskDataMock)
        assertEquals(5, agent.getShip().cargo.units)
        assertEquals(5, agent.getShip().cargo.inventory.firstOrNull{it.symbol == "COPPER_ORE"}?.units)

        assertEquals(0, transActions.size)

    }

    @Test
    fun `run MiningSurvey Full cargo local sell, transport and jettison`() {
        clear()

        val systemId = "systemId"
        val asteroidFieldId = "asteroidFieldId"

        val shipId1 = "test1"
        val agent = createStaticMinerAgent(
            ship = addShip(
                Shipbuilder(dateHelperMock)
                    .setSymbol(shipId1)
                    .setNav(
                        Ship.Nav(
                            status = Ship.Nav.Statue.InOrbit,
                            waypointSymbol = asteroidFieldId,
                            systemSymbol = systemId,
                            route = Ship.Nav.NavRoute(
                                arrival = dateHelperMock.getNow()
                            )
                        )
                    )
                    .setCargo(
                        capacity = 30,
                        inventory = listOf(
                            Ship.Cargo.InventoryItem(
                                symbol = "IRON_ORE",
                                units = 10
                            ),
                            Ship.Cargo.InventoryItem(
                                symbol = "COPPER_ORE",
                                units = 10
                            ),
                            Ship.Cargo.InventoryItem(
                                symbol = "ALUMINUM_ORE",
                                units = 10
                            )
                        )
                    )
                    .build()
            )
        )

        val transportShipId = "transportShip"
        val transportShipController = ShipControllerImp(
            ship = addShip(
                Shipbuilder(dateHelperMock)
                    .setSymbol(transportShipId)
                    .setNav(
                        Ship.Nav(
                            status = Ship.Nav.Statue.InOrbit,
                            waypointSymbol = asteroidFieldId,
                            systemSymbol = systemId,
                            route = Ship.Nav.NavRoute(
                                arrival = dateHelperMock.getNow()
                            )
                        )
                    )
                    .setCargo(
                        capacity = 20,
                        inventory = listOf()
                    )
                    .build()
            ),
            fleetService = fleetServiceMock,
            contractService = contractServiceMock,
            dateHelper = dateHelperMock
        )

        val taskDataMock = TaskDataMock(
            useSurvey = false,
            miningSurvey = MiningSurvey(expiration = Date(0)),
            cargoShipController = transportShipController,
            cargoShipAcceptableCargoList = listOf("COPPER_ORE")
        )

        createAndAddMarketHistory(
            waypointId = asteroidFieldId,
            tradeGoods = listOf(
                TradeGood(
                    symbol = "IRON_ORE",
                    sellPrice = 2
                )
            )
        )

        fleetServiceMock.extractYield = ExtractResponse.Yield(
            symbol = "LEAD_ORE",
            units = 5
        )

        agent.run(taskDataMock)
        assertEquals(5, agent.getShip().cargo.units, "agent cargo has correct units")
        assertEquals(5, agent.getShip().cargo.inventory.firstOrNull{it.symbol == "LEAD_ORE"}?.units, "agent cargo has LEAD_ORE")

        val transaction = transActions.firstOrNull()
        assertEquals(10, transaction?.units)
        assertEquals(Transaction.Type.Sell, transaction?.type, "transaction has correct units")
        assertEquals("IRON_ORE", transaction?.tradeSymbol, "transaction has IRON_ORE")

        assertEquals(10, shipMap[transportShipId]?.cargo?.units, "transport has correct units")
        assertEquals(10, shipMap[transportShipId]?.cargo?.inventory?.firstOrNull{it.symbol == "COPPER_ORE"}?.units, "transport has COPPER_ORE")

    }
}