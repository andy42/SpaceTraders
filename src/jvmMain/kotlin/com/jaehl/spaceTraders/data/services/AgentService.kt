package com.jaehl.spaceTraders.data.services

import com.jaehl.spaceTraders.data.model.Agent
import com.jaehl.spaceTraders.data.remote.SpaceTradersApi
import com.jaehl.spaceTraders.data.remote.baseBody
import javax.inject.Inject


interface AgentService {
    fun getAgent() : Agent
}

class AgentServiceImp @Inject constructor(
    private val authService : AuthService,
    private val spaceTradersApi : SpaceTradersApi
) : AgentService{

     override fun getAgent() : Agent {
         val bearerToken = authService.getBearerToken()
         return spaceTradersApi.getAgent(bearerToken = bearerToken).baseBody()
     }
}