package com.jaehl.spaceTraders.ui.pages.runTask.agents

import com.jaehl.spaceTraders.data.controllers.ShipController
import com.jaehl.spaceTraders.data.controllers.ShipState
import com.jaehl.spaceTraders.data.model.Ship
import com.jaehl.spaceTraders.data.repo.MarketRepo
import com.jaehl.spaceTraders.data.repo.SystemRepo
import com.jaehl.spaceTraders.ui.pages.runTask.tasks.TaskInterface
import com.jaehl.spaceTraders.util.DateHelper
import java.util.LinkedList

class MarketScoutAgent (
    private val shipController : ShipController,
    private val marketRepo: MarketRepo,
    private val systemRepo: SystemRepo,
    private val dateHelper: DateHelper
) : ShipAgent, ShipAgentFinish{

    private val markets = LinkedList<String>()
    private var state : ShipState = ShipState.Wait
    private var stateDescription = ""

    override fun getShipController(): ShipController {
        return shipController
    }

    override fun getShip(): Ship {
        return shipController.getShip()
    }

    private fun moveToNextMarket(){
        if(markets.isEmpty()) return
        val nextMarket = markets.pop()
        shipController.navigate(nextMarket)
        stateDescription = "traveling to market : $nextMarket"
    }

    override fun setup(taskInterface: TaskInterface) {
        shipController.enterOrbit()
        val starSystem = systemRepo.getStarSystem(shipController.getShip().nav.systemSymbol) ?: throw Exception("missing star system data : ${shipController.getShip().nav.systemSymbol}")
        starSystem.waypoints.forEach { waypoint ->
            if(waypoint.hasMarketplace()){
                markets.add(waypoint.symbol)
            }
        }
        stateDescription = "Waiting"
        state = ShipState.Wait
    }

    override fun run(taskInterface: TaskInterface) {
        if (!shipController.getCoolDown().isFinished(dateHelper)) return
        if(state == ShipState.Traveling){
            marketRepo.updateMarket(
                systemId = getShip().nav.systemSymbol,
                waypointId = getShip().nav.waypointSymbol)
            moveToNextMarket()
        }
    }

    override fun calculateState(taskInterface: TaskInterface) {
        if (!shipController.getCoolDown().isFinished(dateHelper)) return
        if(state == ShipState.Wait){
            val currentMarket = markets.firstOrNull { it == getShip().nav.waypointSymbol }
            if(currentMarket != null){
                marketRepo.updateMarket(
                    systemId = getShip().nav.systemSymbol,
                    waypointId = getShip().nav.waypointSymbol)
            }
            markets.remove(currentMarket)
            moveToNextMarket()
            state = ShipState.Traveling
        }
    }

    override fun getAgentDetails(): AgentDetails {
        return AgentDetails(
            ship = getShip(),
            coolDown = getShipController().getCoolDown(),
            stateDescription = stateDescription
        )
    }

    override fun acceptCargo(systemId: String, waypointId: String, cargoId: String): Boolean {
        return false
    }

    override fun hasFinished(): Boolean {
        return markets.isEmpty() && shipController.getCoolDown().isFinished(dateHelper = dateHelper)
    }
}