package com.jaehl.spaceTraders.ui.pages.runTask.agents

import com.jaehl.spaceTraders.data.DataMock
import com.jaehl.spaceTraders.data.controllers.ShipControllerImp
import com.jaehl.spaceTraders.data.model.Ship
import com.jaehl.spaceTraders.data.model.StarSystem
import com.jaehl.spaceTraders.data.model.TradeGood
import com.jaehl.spaceTraders.data.services.mock.Shipbuilder
import com.jaehl.spaceTraders.ui.pages.runTask.tasks.TaskInterface
import kotlin.test.Test
import kotlin.test.assertEquals

class MarketScoutAgentTest {

    private val dataMock = DataMock()

    private fun createMarketScoutAgent(ship : Ship) : MarketScoutAgent {
        return MarketScoutAgent(
            shipController = ShipControllerImp(
                ship = ship,
                fleetService = dataMock.fleetService,
                contractService = dataMock.contractService,
                dateHelper = dataMock.dateHelper
            ),
            dateHelper = dataMock.dateHelper,
            marketRepo = dataMock.marketRepo,
            systemRepo = dataMock.systemRepo
        )
    }

    class TaskDataMock(
    ) : TaskInterface {

    }
    val taskDataMock = TaskDataMock()

    @Test
    fun `starting at a none marketplace`() {
        dataMock.clear()

        val starSystemId = "starSystem"
        val asteroidFieldId = "asteroidField"
        val marketWaypoint1 = "marketWaypoint1"
        val marketWaypoint2 = "marketWaypoint2"

        val scoutShipId = "scoutShip"

        val agent = createMarketScoutAgent(
            dataMock.addShip(
                Shipbuilder(dataMock.dateHelper)
                    .setSymbol(scoutShipId)
                    .setNav(
                        Ship.Nav(
                            status = Ship.Nav.Statue.InOrbit,
                            waypointSymbol = asteroidFieldId,
                            systemSymbol = starSystemId,
                            route = Ship.Nav.NavRoute(
                                arrival = dataMock.dateHelper.getNow()
                            )

                        )
                    )
                    .build()
            )
        )

        dataMock.createAndAddMarketHistory(
            waypointId = marketWaypoint1,
            tradeGoods = listOf(
                TradeGood(
                    symbol = "COPPER_ORE",
                    sellPrice = 2
                )
            )
        )

        dataMock.createAndAddMarketHistory(
            waypointId = marketWaypoint2,
            tradeGoods = listOf(
                TradeGood(
                    symbol = "IRON_ORE",
                    sellPrice = 2
                )
            )
        )

        dataMock.addStarSystem(
            starSystem = StarSystem(
                symbol = starSystemId,
                waypoints = listOf(
                    dataMock.createAsteroidFieldWaypoint(asteroidFieldId, withMarket = false),
                    dataMock.createPlanetWaypoint(marketWaypoint1, withMarket = true),
                    dataMock.createPlanetWaypoint(marketWaypoint2, withMarket = true)
                )
            )
        )

        dataMock.dateHelper.advanceNowBy(1)
        agent.setup(taskDataMock)
        agent.calculateState(taskDataMock)

        assertEquals(Ship.Nav.Statue.InTransit, dataMock.getShip(scoutShipId)?.nav?.status)
        assertEquals(marketWaypoint1, dataMock.getShip(scoutShipId)?.nav?.waypointSymbol)
        assertEquals(false, agent.hasFinished())


        dataMock.dateHelper.advanceNowBy(1)
        agent.run(taskDataMock)

        assertEquals(dataMock.dateHelper.getNow().time, dataMock.getMarketHistory(systemId = starSystemId, waypointId = marketWaypoint1)?.lastUpdate?.time)
        assertEquals(Ship.Nav.Statue.InTransit, dataMock.getShip(scoutShipId)?.nav?.status)
        assertEquals(marketWaypoint2, dataMock.getShip(scoutShipId)?.nav?.waypointSymbol)
        assertEquals(false, agent.hasFinished())


        dataMock.dateHelper.advanceNowBy(1)
        agent.run(taskDataMock)

        assertEquals(dataMock.dateHelper.getNow().time, dataMock.getMarketHistory(systemId = starSystemId, waypointId = marketWaypoint2)?.lastUpdate?.time)
        assertEquals(true, agent.hasFinished())
    }

    @Test
    fun `starting at a marketplace`() {
        dataMock.clear()

        val starSystemId = "starSystem"
        val asteroidFieldId = "asteroidField"
        val marketWaypoint1 = "marketWaypoint1"
        val marketWaypoint2 = "marketWaypoint2"

        val scoutShipId = "scoutShip"

        val agent = createMarketScoutAgent(
            dataMock.addShip(
                Shipbuilder(dataMock.dateHelper)
                    .setSymbol(scoutShipId)
                    .setNav(
                        Ship.Nav(
                            status = Ship.Nav.Statue.InOrbit,
                            waypointSymbol = asteroidFieldId,
                            systemSymbol = starSystemId,
                            route = Ship.Nav.NavRoute(
                                arrival = dataMock.dateHelper.getNow()
                            )
                        )
                    )
                    .build()
            )
        )

        dataMock.createAndAddMarketHistory(
            waypointId = asteroidFieldId,
            tradeGoods = listOf(
                TradeGood(
                    symbol = "COPPER_ORE",
                    sellPrice = 4
                )
            )
        )

        dataMock.createAndAddMarketHistory(
            waypointId = marketWaypoint1,
            tradeGoods = listOf(
                TradeGood(
                    symbol = "COPPER_ORE",
                    sellPrice = 2
                )
            )
        )

        dataMock.createAndAddMarketHistory(
            waypointId = marketWaypoint2,
            tradeGoods = listOf(
                TradeGood(
                    symbol = "IRON_ORE",
                    sellPrice = 2
                )
            )
        )

        dataMock.addStarSystem(
            starSystem = StarSystem(
                symbol = starSystemId,
                waypoints = listOf(
                    dataMock.createAsteroidFieldWaypoint(asteroidFieldId, withMarket = true),
                    dataMock.createPlanetWaypoint(marketWaypoint1, withMarket = true),
                    dataMock.createPlanetWaypoint(marketWaypoint2, withMarket = true)
                )
            )
        )

        dataMock.dateHelper.advanceNowBy(1)
        agent.setup(taskDataMock)
        agent.calculateState(taskDataMock)

        assertEquals(dataMock.dateHelper.getNow().time, dataMock.getMarketHistory(systemId = starSystemId, waypointId = asteroidFieldId)?.lastUpdate?.time)
        assertEquals(Ship.Nav.Statue.InTransit, dataMock.getShip(scoutShipId)?.nav?.status)
        assertEquals(marketWaypoint1, dataMock.getShip(scoutShipId)?.nav?.waypointSymbol)
        assertEquals(false, agent.hasFinished())

        dataMock.dateHelper.advanceNowBy(1)
        agent.run(taskDataMock)

        assertEquals(dataMock.dateHelper.getNow().time, dataMock.getMarketHistory(systemId = starSystemId, waypointId = marketWaypoint1)?.lastUpdate?.time)
        assertEquals(Ship.Nav.Statue.InTransit, dataMock.getShip(scoutShipId)?.nav?.status)
        assertEquals(marketWaypoint2, dataMock.getShip(scoutShipId)?.nav?.waypointSymbol)
        assertEquals(false, agent.hasFinished())

        dataMock.dateHelper.advanceNowBy(1)
        agent.run(taskDataMock)

        assertEquals(dataMock.dateHelper.getNow().time, dataMock.getMarketHistory(systemId = starSystemId, waypointId = marketWaypoint2)?.lastUpdate?.time)
        assertEquals(true, agent.hasFinished())
    }
}