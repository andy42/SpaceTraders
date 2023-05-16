package com.jaehl.spaceTraders.ui.pages.shipDetails.tasks

import com.jaehl.spaceTraders.data.model.Market
import com.jaehl.spaceTraders.data.model.SystemWaypoint
import com.jaehl.spaceTraders.data.model.WaypointType
import com.jaehl.spaceTraders.data.model.response.JumpGateResponse
import com.jaehl.spaceTraders.data.services.FleetService
import com.jaehl.spaceTraders.data.services.SystemService
import com.jaehl.spaceTraders.util.Logger
import kotlinx.coroutines.delay
import javax.inject.Inject

class MarketSearchTask @Inject constructor(
    private val logger : Logger,
    private val fleetService : FleetService,
    private val systemService: SystemService
) {

    suspend fun getJumpGate(systemId: String, waypointId: String) : JumpGateResponse{
        val response = systemService.getJumpGate(systemId, waypointId)
        delay(1000)
        return response
    }

    suspend fun getSystemWaypoints(systemId : String) : List<SystemWaypoint> {
        val response = systemService.getSystemWaypoints(systemId, 1)
        delay(1000)
        return response.data
    }

    suspend fun getMarket(systemId : String, waypointId: String) : Market {
        val response = systemService.getMarket(systemId, waypointId)
        delay(1000)
        return response
    }

    suspend fun searchMarketFor(startSystem : String, range : Int) {
        val jumpGateWaypointId = getSystemWaypoints(startSystem).firstOrNull{ it.type == WaypointType.jumpGate }?.symbol ?: return
        val jumpGate = getJumpGate(startSystem, jumpGateWaypointId)

        jumpGate.connectedSystems.forEach { connectedSystem ->
            if (connectedSystem.distance < range) {
                val markets = getSystemWaypoints(connectedSystem.symbol).filter {
                    it.hasMarketplace()
                }
                markets.forEach {
                    val market = getMarket(it.systemSymbol, it.symbol)
                    logger.log("system : ${it.systemSymbol} waypoint : ${it.symbol}")
                    logger.log(market.exports.map { it.name }.joinToString(", "))
                    logger.log("")
                }
            }
        }
    }

    suspend fun distance(startSystem : String, systems : List<String>) {
        val jumpGateWaypointId = getSystemWaypoints(startSystem).firstOrNull{ it.type == WaypointType.jumpGate }?.symbol ?: return
        val jumpGate = getJumpGate(startSystem, jumpGateWaypointId)
        jumpGate.connectedSystems.forEach { connectedSystem ->
            if(systems.contains(connectedSystem.symbol)){
                logger.log("${connectedSystem.symbol} : ${connectedSystem.distance}")
            }
        }
    }



}