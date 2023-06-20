package com.jaehl.spaceTraders.ui.pages.runTask.agents

import com.jaehl.spaceTraders.data.controllers.ShipController
import com.jaehl.spaceTraders.data.model.Ship
import com.jaehl.spaceTraders.data.model.response.Cooldown
import com.jaehl.spaceTraders.ui.pages.runTask.tasks.TaskInterface
import com.jaehl.spaceTraders.ui.pages.shipDetails.tasks.OreMineRefineSellTask

data class AgentDetails(
    val ship : Ship,
    val coolDown: Cooldown,
    val stateDescription : String
)

interface ShipAgent {
    fun getShipController() : ShipController
    fun getShip() : Ship
    fun setup(taskInterface : TaskInterface)
    fun run(taskInterface : TaskInterface)
    fun calculateState(taskInterface : TaskInterface)
    fun getAgentDetails() : AgentDetails
    fun acceptCargo(systemId: String, waypointId : String, cargoId : String) : Boolean
}

interface ShipAgentFinish {
    fun hasFinished() : Boolean
}