package com.jaehl.spaceTraders.ui.pages.systemSearch

import com.jaehl.spaceTraders.data.model.SystemWaypoint
import com.jaehl.spaceTraders.data.model.Vector2d
import com.jaehl.spaceTraders.data.repo.MarketRepo
import com.jaehl.spaceTraders.data.repo.ShipyardRepo
import com.jaehl.spaceTraders.data.repo.SystemRepo
import com.jaehl.spaceTraders.data.services.SystemService
import com.jaehl.spaceTraders.ui.util.ViewModel
import com.jaehl.spaceTraders.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.jaehl.spaceTraders.ui.navigation.NavBackListener
import kotlinx.coroutines.delay

class SystemSearchViewModel @Inject constructor(
    private val logger : Logger,
    private val systemService : SystemService,
    private val systemRepo : SystemRepo,
    private val marketRepo: MarketRepo,
    private val shipyardRepo : ShipyardRepo
) : ViewModel() {

    var navBackListener : NavBackListener? = null

    override fun init(viewModelScope: CoroutineScope) {
        super.init(viewModelScope)

        viewModelScope.launch {

        }
    }

    private suspend fun updateShipyards(){
        logger.log("updateShipyards ")

        val homeSystem = systemRepo.getStarSystem("X1-ZA40")?.getPosition() ?: return

        var shipyards = arrayListOf<SystemWaypoint>()
        systemRepo.getStarSystems().forEach {
            shipyards.addAll(it.waypoints.filter { it.hasShipyard() })
        }
        logger.log("shipyards found ${shipyards.size} ")
        shipyards.forEachIndexed { index, shipyardWaypoint ->
            val system = systemRepo.getStarSystem(shipyardWaypoint.systemSymbol)?.getPosition()
            system?.let {
                val response = shipyardRepo.update(shipyardWaypoint.systemSymbol, shipyardWaypoint.symbol)
                logger.log("$index, waypointSymbol:${shipyardWaypoint.symbol} distance:${system.distance(homeSystem).toInt()} ${response.shipyard.shipTypes.map { it.type }.joinToString(separator = ", ")}")
                delay(600)
            }
        }
        logger.log("Finished")
    }


    fun onBackClick() {
        navBackListener?.navigateBack()
    }
}