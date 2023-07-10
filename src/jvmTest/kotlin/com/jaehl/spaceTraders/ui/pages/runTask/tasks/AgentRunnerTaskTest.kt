package com.jaehl.spaceTraders.ui.pages.runTask.tasks

import com.jaehl.spaceTraders.data.model.*
import com.jaehl.spaceTraders.data.model.response.Cooldown
import com.jaehl.spaceTraders.data.model.response.ExtractResponse
import com.jaehl.spaceTraders.data.repo.MarketRepo
import com.jaehl.spaceTraders.data.repo.mock.MarketRepoMock
import com.jaehl.spaceTraders.data.repo.mock.SystemRepoMock
import com.jaehl.spaceTraders.data.services.mock.ContractServiceMock
import com.jaehl.spaceTraders.data.services.mock.FleetServiceMock
import com.jaehl.spaceTraders.data.services.mock.Shipbuilder
import com.jaehl.spaceTraders.ui.pages.runTask.agents.AgentDetails
import com.jaehl.spaceTraders.util.DateHelperMock
import com.jaehl.spaceTraders.util.Logger
import com.jaehl.spaceTraders.util.LoggerImp
import io.kotest.common.runBlocking
import org.junit.Test
import java.util.ArrayList
import java.util.Date
import kotlin.test.assertEquals

class AgentRunnerTaskTest {

    private var logger = LoggerImp()
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


    private val systemRepoMock = SystemRepoMock()
    private val marketRepoMock = MarketRepoMock(dateHelperMock)



    private fun clear() {
        shipMap.clear()
        transActions.clear()
        shipCoolDowns.clear()
        refineDataMap.clear()
        contractMap.clear()
        systemRepoMock.clear()
        marketRepoMock.clear()
    }

    private fun createAgentRunnerTask() : AgentRunnerTask {
        return AgentRunnerTask(
            logger = logger,
            fleetService = fleetServiceMock,
            contractService = contractServiceMock,
            systemRepo = systemRepoMock,
            marketRepo = marketRepoMock,
            dateHelper = dateHelperMock
        )
    }

    class TaskUpdateListenerMock() : TaskUpdateListener{
        var agentDetailsList = listOf<AgentDetails>()
        override fun onTaskUpdate(agentDetailsList : List<AgentDetails>){
            this.agentDetailsList = agentDetailsList
        }
    }

    private fun addShip(ship : Ship) : Ship{
        shipMap[ship.symbol] = ship
        return ship
    }

    private fun createAsteroidField(waypointId : String) : SystemWaypoint{
        return SystemWaypoint(
            symbol = waypointId,
            type = WaypointType.asteroidField,
            traits = listOf(WaypointTrait(symbol = TraitType.Marketplace))
        )
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

    @Test
    fun `test`() = runBlocking {
        clear()

        val taskUpdateListenerMock = TaskUpdateListenerMock()
        val agentRunnerTask = createAgentRunnerTask()


        val starSystemId = "starSystemId"

        val asteroidFieldId = "asteroidField"
        systemRepoMock.addStarSystem(
            StarSystem(
                symbol = starSystemId,
                waypoints = listOf(
                    createAsteroidField(asteroidFieldId)
                )
            )
        )

        createAndAddMarketHistory(
            waypointId = asteroidFieldId,
            tradeGoods = listOf(
                TradeGood(
                    symbol = "COPPER_ORE",
                    sellPrice = 2
                )
            )
        )

        val shipId1 = "ship1"
        addShip(
            Shipbuilder(dateHelperMock)
                .setSymbol(shipId1)
                .setNav(
                    Ship.Nav(
                        systemSymbol = starSystemId,
                        waypointSymbol = asteroidFieldId,
                        status = Ship.Nav.Statue.InOrbit,
                        route = Ship.Nav.NavRoute(
                            arrival = dateHelperMock.getNow()
                        )
                    )
                )
                .setCargo(10, listOf())
                .build()
        )

        dateHelperMock.setNow(Date(0))

        fleetServiceMock.extractYield = ExtractResponse.Yield(
            symbol = "COPPER_ORE",
            units = 5
        )

        agentRunnerTask.setups(
            agentRoleList = listOf(
                AgentRole(
                    shipId = shipId1,
                    role = AgentRole.Role.StaticMiner
                )
            ),
            taskUpdateListener = taskUpdateListenerMock
        )

        agentRunnerTask.runIteration()


        var ship = shipMap[shipId1]

        assertEquals(5, shipMap[shipId1]?.cargo?.units)

        dateHelperMock.advanceNowBy(seconds = 1)
        agentRunnerTask.runIteration()
        assertEquals(10, shipMap[shipId1]?.cargo?.units)

        dateHelperMock.advanceNowBy(seconds = 1)
        agentRunnerTask.runIteration()
        assertEquals(5, shipMap[shipId1]?.cargo?.units)
        assertEquals(10, transActions.firstOrNull()?.units, "expects the transaction of 10 units of copper")

    }

    @Test
    fun `transfer to transport agent & Sell`() {

    }
}