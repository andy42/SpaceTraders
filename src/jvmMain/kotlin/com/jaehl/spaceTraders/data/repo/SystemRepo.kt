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

@Singleton
class SystemRepo @Inject constructor(
    private val logger: Logger,
    private val systemService : SystemService,
    private val systemDataLoader : ObjectListLoader<StarSystem>,
    localFileConfig : LocalFileConfig
) {
    private val file : File = localFileConfig.getSystemsFile()
    private val starSystemMap = LinkedHashMap<String, StarSystem>()
    private var loaded = false

    init {
        loadLocal(true)
    }

    fun getStarSystems() : List<StarSystem> {
        return starSystemMap.values.toList()
    }

    fun getStarSystem(systemId : String) : StarSystem?{
        return starSystemMap[systemId]
    }

    fun testUpdate(starSystem : StarSystem){
        starSystemMap[starSystem.symbol]?.let {
            starSystemMap[starSystem.symbol] = it.copy(
                x = starSystem.x,
                y = starSystem.y
            )
        }
    }

    fun save() {
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

    suspend fun deapLoadAll() {
        val systems = systemService.getSystems()
        systems.forEachIndexed { index, starSystem ->
            logger.log("deapLoadAll : $index")
            val waypointsResponse1 = systemService.getSystemWaypoints(starSystem.symbol, 1)
            var waypoints = waypointsResponse1.data.toMutableList()

            if(starSystem.waypoints.size > 10) {
                val waypointsResponse2 = systemService.getSystemWaypoints(starSystem.symbol, 2)
                waypoints.addAll(waypointsResponse2.data)
            }

            delay(550)
            starSystemMap[starSystem.symbol] = starSystem.copy(
                waypoints = waypoints
            )
        }
        systemDataLoader.save(file, starSystemMap.values.toList())
    }
}