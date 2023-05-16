package com.jaehl.spaceTraders.data.services

import com.jaehl.spaceTraders.data.model.Market
import com.jaehl.spaceTraders.data.model.Shipyard
import com.jaehl.spaceTraders.data.model.StarSystem
import com.jaehl.spaceTraders.data.model.SystemWaypoint
import com.jaehl.spaceTraders.data.model.response.JumpGateResponse
import com.jaehl.spaceTraders.data.remote.ResponsePaged
import com.jaehl.spaceTraders.data.remote.SpaceTradersApi
import com.jaehl.spaceTraders.data.remote.baseBody
import com.jaehl.spaceTraders.data.remote.pagedBody
import javax.inject.Inject

interface SystemService {
    fun getSystems() : List<StarSystem>
    fun getSystem(systemId: String) : StarSystem
    fun getSystemWaypoints(systemId: String, page : Int = 1) : ResponsePaged<SystemWaypoint>
    fun getMarket(systemId: String, waypointId: String) : Market
    fun getJumpGate(systemId: String, waypointId: String) : JumpGateResponse
    fun getShipyard(systemId: String, waypointId: String) : Shipyard
}

class SystemServiceImp  @Inject constructor(
    private val authService : AuthService,
    private val spaceTradersApi : SpaceTradersApi
) : SystemService{

    override fun getSystems(): List<StarSystem> {
        val request = spaceTradersApi.getAllSystem()
        val response = request.execute()
        if(response.isSuccessful) return response.body() ?: listOf()
        else throw Throwable(response.errorBody()?.string())
    }

    override fun getSystem(systemId: String): StarSystem {
        val bearerToken = authService.getBearerToken()
        return spaceTradersApi.getSystem(bearerToken = bearerToken, systemSymbol = systemId).baseBody()
    }

    override fun getSystemWaypoints(systemId: String, page : Int): ResponsePaged<SystemWaypoint> {
        val bearerToken = authService.getBearerToken()
        return spaceTradersApi.getSystemWaypoints(bearerToken = bearerToken, systemSymbol = systemId, limit = PageLimit, page = page).pagedBody()
    }

    override fun getMarket(systemId: String, waypointId: String): Market {
        val bearerToken = authService.getBearerToken()
        return spaceTradersApi.getMarket(bearerToken = bearerToken, systemSymbol = systemId, waypointSymbol = waypointId).baseBody()
    }

    override fun getShipyard(systemId: String, waypointId: String): Shipyard {
        val bearerToken = authService.getBearerToken()
        return spaceTradersApi.getShipyard(bearerToken = bearerToken, systemSymbol = systemId, waypointSymbol = waypointId).baseBody()
    }

    override fun getJumpGate(systemId: String, waypointId: String): JumpGateResponse {
        val bearerToken = authService.getBearerToken()
        return spaceTradersApi.getJumpGate(bearerToken = bearerToken, systemSymbol = systemId, waypointSymbol = waypointId).baseBody()
    }

    companion object {
        private const val PageLimit = 20
    }
}