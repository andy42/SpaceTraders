package com.jaehl.spaceTraders.data.repo

import com.jaehl.spaceTraders.data.local.LocalFileConfig
import com.jaehl.spaceTraders.data.local.ObjectListJsonLoader
import com.jaehl.spaceTraders.data.model.Agent
import com.jaehl.spaceTraders.data.model.AgentLocal
import com.jaehl.spaceTraders.data.model.FactionName
import com.jaehl.spaceTraders.data.services.AgentService
import com.jaehl.spaceTraders.util.Logger
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AgentRepo @Inject constructor(
    private val logger: Logger,
    private val agentLoader : ObjectListJsonLoader<AgentLocal>,
    private val localFileConfig : LocalFileConfig,
    private val agentService: AgentService,

) {
    private val file : File = localFileConfig.getAgentFile()
    private val agentMap = LinkedHashMap<String, AgentLocal>()
    private var loaded = false

    init {
        loadLocal(true)
    }

    fun createNew(callsign : String, factionName: FactionName, email : String){
        val response = agentService.register(callsign, factionName, email)
        agentMap[response.agent.symbol] = AgentLocal(
            agent = response.agent,
            token = response.token
        )
        save()
    }

    fun createNew(token : String){
        val agent = agentService.getAgent(token = token)
        agentMap[agent.symbol] = AgentLocal(
            agent = agent,
            token = token
        )
        save()
    }

    fun getAgentToken(agentId : String) : String{
        return agentMap[agentId]?.token ?: ""
    }

    fun getFirstAgent() : Agent?{
        return agentMap.values.firstOrNull()?.agent
    }

    fun getAgent(agentId : String) : Agent?{
        return agentMap[agentId]?.agent
    }

    fun updateAgent(agentId: String) : Agent?{
        val agent = agentService.getAgent()
        agentMap[agentId]?.let {
            agentMap[agentId] = it.copy(
                agent = agent
            )
        }
        return agentMap[agentId]?.agent
    }

    private fun save(){
        agentLoader.save(file, agentMap.values.toList())
    }

    private fun loadLocal(forceReload : Boolean = false){
        if(loaded && !forceReload) return
        try {
            agentMap.clear()
            agentLoader.load(file).forEach {
                agentMap[it.agent.symbol] = it
            }
            loaded = true

        } catch (t : Throwable){
            logger.error("AgentRepo ${t.message}")
        }
    }
}