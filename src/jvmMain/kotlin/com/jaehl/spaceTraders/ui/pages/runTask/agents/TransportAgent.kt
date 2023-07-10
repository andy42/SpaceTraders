package com.jaehl.spaceTraders.ui.pages.runTask.agents

import com.jaehl.spaceTraders.data.controllers.ShipController
import com.jaehl.spaceTraders.data.model.Ship
import com.jaehl.spaceTraders.ui.pages.runTask.tasks.TaskInterface
import com.jaehl.spaceTraders.util.DateHelper

class TransportAgent (
    private val shipController : ShipController,
    private val dateHelper: DateHelper
) : ShipAgent {

    override fun getShipController(): ShipController {
        return shipController
    }

    override fun getShip(): Ship {
        return shipController.getShip()
    }

    override fun setup(taskInterface: TaskInterface) {

    }

    override fun run(taskInterface: TaskInterface) {

    }

    override fun calculateState(taskInterface: TaskInterface) {

    }

    override fun getAgentDetails(): AgentDetails {
        return AgentDetails(
            ship = shipController.getShip(),
            coolDown = shipController.getCoolDown(),
            stateDescription = ""
        )
    }

    override fun acceptCargo(systemId: String, waypointId: String, cargoId: String): Boolean {
        return true
    }
}