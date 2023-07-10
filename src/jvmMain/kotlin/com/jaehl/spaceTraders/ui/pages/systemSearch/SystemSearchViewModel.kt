package com.jaehl.spaceTraders.ui.pages.systemSearch

import com.jaehl.spaceTraders.data.model.StarSystem
import com.jaehl.spaceTraders.data.model.SystemWaypoint
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
    }

    private fun distance(first : StarSystem, second : StarSystem) : Float {
        return first.getPosition().distance(second.getPosition())
    }

    private suspend fun getWaypoints(startSystemId : String, destinationSystemId : String) : List<String>{

        val startSystem = systemRepo.getStarSystem(startSystemId) ?: return listOf()
        val destinationSystem = systemRepo.getStarSystem(destinationSystemId) ?: return listOf()
        val distance = startSystem.getPosition().distance(destinationSystem.getPosition())

        var jumpGateSystems = arrayListOf<StarSystem>()
        systemRepo.getStarSystems().forEach {system ->
            if(system.symbol == startSystem.symbol) return@forEach
            if(destinationSystem.symbol == system.symbol) return@forEach
            if(startSystem.getPosition().distance(system.getPosition()) > distance) return@forEach
            if(destinationSystem.getPosition().distance(system.getPosition()) > distance) return@forEach

            jumpGateSystems.add(system)
        }

        val maxRange = 5000f
        var destinationDistance = distance
        var foundSystem : StarSystem? = null
        jumpGateSystems.forEach { system ->
            val newDistance = distance(startSystem, system)
            val newDistance2 = distance(destinationSystem, system)


            if(newDistance2 < destinationDistance && newDistance < maxRange){
                logger.log("${system.symbol} distance : ${destinationSystem.getPosition().distance(system.getPosition())}")
                destinationDistance = newDistance
                foundSystem = system
            }
        }

        if(foundSystem != null) {
            return listOf(foundSystem?.symbol ?: "")
        }

        return listOf()
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