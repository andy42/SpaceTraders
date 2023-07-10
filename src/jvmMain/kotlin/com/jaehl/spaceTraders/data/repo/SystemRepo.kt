package com.jaehl.spaceTraders.data.repo

import com.jaehl.spaceTraders.data.local.LocalFileConfig
import com.jaehl.spaceTraders.data.local.ObjectListLoader
import com.jaehl.spaceTraders.data.model.StarSystem
import com.jaehl.spaceTraders.data.services.SystemService
import com.jaehl.spaceTraders.util.Logger
import kotlinx.coroutines.delay
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

interface SystemRepo {
    fun getStarSystems() : List<StarSystem>
    fun getStarSystem(systemId : String) : StarSystem?
    suspend fun deapLoadAll()
}

class SystemRepoImp @Inject constructor (
    private val logger: Logger,
    private val systemService : SystemService,
    private val systemDataLoader : ObjectListLoader<StarSystem>,
    localFileConfig : LocalFileConfig
) : SystemRepo{
    private val file : File = localFileConfig.getSystemsFile()
    private val starSystemMap = LinkedHashMap<String, StarSystem>()
    private var loaded = false

    init {
        loadLocal(true)
    }

    override fun getStarSystems() : List<StarSystem> {
        return starSystemMap.values.toList()
    }

    override fun getStarSystem(systemId : String) : StarSystem?{
        return starSystemMap[systemId] ?: deepLoadStarSystem(systemId)
    }

    private fun deepLoadStarSystem(systemId : String) : StarSystem?{
        val starSystem = systemService.getSystem(systemId)

        val waypointsResponse1 = systemService.getSystemWaypoints(starSystem.symbol, 1)
        var waypoints = waypointsResponse1.data.toMutableList()

        if(starSystem.waypoints.size > 10) {
            val waypointsResponse2 = systemService.getSystemWaypoints(starSystem.symbol, 2)
            waypoints.addAll(waypointsResponse2.data)
        }
        starSystemMap[starSystem.symbol] = starSystem.copy(
            waypoints = waypoints
        )
        save()
        return starSystem
    }

    private fun save() {
        systemDataLoader.save(file, starSystemMap.values.toList())
    }

    private fun loadLocal(forceReload : Boolean = false){
        if(loaded && !forceReload) return
        try {
            starSystemMap.clear()
            systemDataLoader.load(file).forEach {
                starSystemMap[it.symbol] = it
            }
            loaded = true

        } catch (t : Throwable){
            logger.error("SystemRepo ${t.message}")
        }
    }

    override suspend fun deapLoadAll() {
        val systems = systemService.getSystems()
        systems.forEachIndexed { index, starSystem ->
            logger.log("deapLoadAll : $index")
            val waypointsResponse1 = systemService.getSystemWaypoints(starSystem.symbol, 1)
            var waypoints = waypointsResponse1.data.toMutableList()

            if(starSystem.waypoints.size > 10) {
                val waypointsResponse2 = systemService.getSystemWaypoints(starSystem.symbol, 2)
                waypoints.addAll(waypointsResponse2.data)
            }
            starSystemMap[starSystem.symbol] = starSystem.copy(
                waypoints = waypoints
            )
        }
        save()
    }
}