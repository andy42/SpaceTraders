package com.jaehl.spaceTraders.data.services

import com.jaehl.spaceTraders.data.model.Agent
import com.jaehl.spaceTraders.data.model.FactionName
import com.jaehl.spaceTraders.data.model.request.RegisterRequest
import com.jaehl.spaceTraders.data.model.response.RegisterResponse
import com.jaehl.spaceTraders.data.remote.SpaceTradersApi
import com.jaehl.spaceTraders.data.remote.baseBody
import javax.inject.Inject


interface AgentService {
    fun getAgent(token : String) : Agent
    fun getAgent() : Agent
    fun register(callsign : String, faction : FactionName, email : String) : RegisterResponse
}

class AgentServiceImp @Inject constructor(
    private val authService : AuthService,
    private val spaceTradersApi : SpaceTradersApi
) : AgentService{

    override fun getAgent(token: String): Agent {
        return spaceTradersApi.getAgent(bearerToken = "Bearer $token").baseBody()
    }

    override fun getAgent() : Agent {
         val bearerToken = authService.getBearerToken()
         return spaceTradersApi.getAgent(bearerToken = bearerToken).baseBody()
     }

    override fun register(callsign : String, faction : FactionName, email : String) : RegisterResponse {
        return spaceTradersApi.register(RegisterRequest(
            faction = faction,
            symbol = callsign,
            email = email
        )).baseBody()
    }
}