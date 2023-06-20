package com.jaehl.spaceTraders.ui.pages.runTask.tasks

import com.jaehl.spaceTraders.data.controllers.ShipController
import com.jaehl.spaceTraders.data.model.MiningSurvey

interface TaskInterface {
}


interface BasicTaskInterface : TaskInterface {
    fun findShipForCargo(shipId :String, systemId : String, waypointId : String, cargoId : String) : ShipController?
}

interface MinerTaskInterface : TaskInterface {
    fun retrieveUseSurvey() : Boolean
    fun retrieveMiningSurvey() : MiningSurvey
    fun retrieveRefineItemList() : List<RefineItem>
    fun retrieveAsteroidWaypoint() : String
}