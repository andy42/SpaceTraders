package com.jaehl.spaceTraders.data.services

import com.jaehl.spaceTraders.data.model.Market
import com.jaehl.spaceTraders.data.model.StarSystem
import com.jaehl.spaceTraders.data.model.SystemWaypoint
import com.jaehl.spaceTraders.data.remote.ResponsePaged
import com.jaehl.spaceTraders.data.remote.SpaceTradersApi
import com.jaehl.spaceTraders.data.remote.baseBody
import com.jaehl.spaceTraders.data.remote.pagedBody
import javax.inject.Inject

interface SystemService {
    fun getSystem(systemId: String) : StarSystem
    fun getSystemWaypoints(systemId: String, page : Int = 1) : ResponsePaged<SystemWaypoint>
    fun getMarket(systemId: String, waypointId: String) : Market
}

class SystemServiceImp  @Inject constructor(
    private val authService : AuthService,
    private val spaceTradersApi : SpaceTradersApi
) : SystemService{

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

    companion object {
        private const val PageLimit = 20
    }
}