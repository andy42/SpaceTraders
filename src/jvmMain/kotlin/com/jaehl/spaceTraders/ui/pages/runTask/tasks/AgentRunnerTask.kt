package com.jaehl.spaceTraders.ui.pages.runTask.tasks

import com.jaehl.spaceTraders.data.controllers.ShipController
import com.jaehl.spaceTraders.data.controllers.ShipControllerImp
import com.jaehl.spaceTraders.data.model.*
import com.jaehl.spaceTraders.data.repo.MarketRepo
import com.jaehl.spaceTraders.data.repo.SystemRepo
import com.jaehl.spaceTraders.data.services.ContractService
import com.jaehl.spaceTraders.data.services.FleetService
import com.jaehl.spaceTraders.ui.pages.runTask.agents.*
import com.jaehl.spaceTraders.util.DateHelper
import com.jaehl.spaceTraders.util.Logger
import kotlinx.coroutines.delay
import javax.inject.Inject

data class AgentRole(
    val shipId : String,
    val role : Role
) {
    enum class Role{
        None,
        StaticMiner,
        Transport,
        TransportMiner,
        MarketScout
    }
}

interface TaskUpdateListener{
    fun onTaskUpdate(agentDetailsList : List<AgentDetails>)
}

class AgentRunnerTask @Inject constructor(
    private val logger : Logger,
    private val fleetService : FleetService,
    private val contractService : ContractService,
    private val systemRepo : SystemRepo,
    private val marketRepo : MarketRepo,
    private val dateHelper : DateHelper

) : BasicTaskInterface, MinerTaskInterface {

    private val agents = arrayListOf<ShipAgent>()
    private val tradeGoodsMap = HashMap<String, List<String>>()

    private var asteroidWaypoint : String = ""

    private var taskUpdateListener : TaskUpdateListener? = null

    private var availableTransport = false

    override fun retrieveUseSurvey(): Boolean {
        return false
    }

    override fun retrieveMiningSurvey(): MiningSurvey {
        return MiningSurvey()
    }

    override fun retrieveRefineItemList(): List<RefineItem> {
        return listOf()
    }

    override fun retrieveAsteroidWaypoint(): String {
        return asteroidWaypoint
    }

    //add logic to prioritise different agents (agent.getCargoPriority) then sort agents
    override fun findShipForCargo(shipId :String, systemId: String, waypointId: String, cargoId: String): ShipController? {
        for(agent in agents){
            if (agent.getShip().symbol == shipId) continue
            if(agent.acceptCargo(systemId = systemId, waypointId= waypointId, cargoId= cargoId)) return agent.getShipController()
        }
        return null
    }

    private fun createShipController(shipId : String) : ShipController {
        return ShipControllerImp(
            ship = fleetService.getShip(shipId),
            fleetService = fleetService,
            contractService = contractService,
            dateHelper = dateHelper
        )
    }

    private fun hasTransportRole(agentRoleList : List<AgentRole>) : Boolean {
        return (agentRoleList.firstOrNull{it.role == AgentRole.Role.Transport || it.role == AgentRole.Role.TransportMiner} != null)
    }

    fun setups(agentRoleList : List<AgentRole>, taskUpdateListener : TaskUpdateListener){
        this.taskUpdateListener = taskUpdateListener
        agentRoleList.forEach { agentRole ->
            val agent = when (agentRole.role) {
                AgentRole.Role.StaticMiner -> {
                    StaticMinerAgent(
                        shipController = createShipController(agentRole.shipId),
                        dateHelper = dateHelper,
                        marketRepo = marketRepo,
                        logger = logger
                    )
                }
                AgentRole.Role.MarketScout -> {
                    MarketScoutAgent(
                        shipController = createShipController(agentRole.shipId),
                        dateHelper = dateHelper,
                        marketRepo = marketRepo,
                        systemRepo = systemRepo
                    )
                }
                else -> {
                    throw Exception("setups, missing agentRole : ${agentRole.role.name}")
                }
            }
            agent?.let {
                agents.add(agent)
            }
        }
        availableTransport = hasTransportRole(agentRoleList)

        val firstShip = agents.firstOrNull()?.getShip() ?: throw Exception("missing any ships")

        val systemId = firstShip.nav.systemSymbol
        val system = systemRepo.getStarSystem(systemId) ?: throw Exception("Missing system : $systemId")
        system.waypoints.forEach { systemWaypoint ->
            if(systemWaypoint.hasMarketplace()){
                marketRepo.getMarketHistory(systemId, systemWaypoint.symbol)?.let { marketHistory ->
                    tradeGoodsMap[marketHistory.symbol] = marketHistory.tradeGoods.map { it.symbol }
                }
            }
            if(systemWaypoint.type == WaypointType.asteroidField){
                asteroidWaypoint = systemWaypoint.symbol
            }
        }
        if(asteroidWaypoint.isEmpty()) throw Exception("Missing asteroidField in system : $systemId")


        agents.forEach { agent ->
            agent.setup(this)
        }
        taskUpdateListener?.onTaskUpdate(agents.map { it.getAgentDetails() })
    }

    private fun getIterationDelay() : Long {
        var delayTime = Long.MAX_VALUE
        agents.forEach {agent ->
            val coolDown = agent.getShipController().getCoolDown()
            if(!coolDown.isFinished(dateHelper) && coolDown.getDelay(dateHelper) < delayTime){
                delayTime = coolDown.getDelay(dateHelper)
            }
        }
        return if(delayTime == Long.MAX_VALUE) 0 else delayTime
    }

    //run Single Iteration returns false if ass agents have finished
    fun runIteration() :Boolean{
        var finished = true
        agents.forEach { agent ->
            agent.calculateState(this)
            agent.run(this)
            finished = (agent as? ShipAgentFinish)?.hasFinished() ?: false && finished
        }

        taskUpdateListener?.onTaskUpdate(agents.map { it.getAgentDetails() })
        return !finished
    }

    suspend fun run(){
        while (true) {
            if(!runIteration()) {
                taskUpdateListener?.onTaskUpdate(listOf())
                break
            }
            val delay = getIterationDelay()
            delay(delay)
        }
    }
}