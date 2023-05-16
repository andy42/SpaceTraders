package com.jaehl.spaceTraders.data.repo

import com.jaehl.spaceTraders.data.local.LocalFileConfig
import com.jaehl.spaceTraders.data.local.ObjectListJsonLoader
import com.jaehl.spaceTraders.data.model.ShipyardSaved
import com.jaehl.spaceTraders.data.services.SystemService
import com.jaehl.spaceTraders.util.Logger
import java.io.File
import javax.inject.Inject

class ShipyardRepo @Inject constructor(
    private val logger: Logger,
    private val shipyardLoader : ObjectListJsonLoader<ShipyardSaved>,
    private val localFileConfig : LocalFileConfig,
    private val systemService: SystemService
) {
    private val file : File = localFileConfig.getShipyardFile()
    private val shipyardMap = LinkedHashMap<String, ShipyardSaved>()
    private var loaded = false

    init {
        loadLocal(true)
    }

    fun getShipyard(waypointId : String) : ShipyardSaved?{
        return shipyardMap[waypointId]
    }

    fun update(systemId: String, waypointId: String) : ShipyardSaved {
        val shipyard = systemService.getShipyard(systemId ,waypointId)
        val saved = shipyardMap[waypointId]
        if(saved == null){

        }
        shipyardMap[waypointId] = ShipyardSaved.create(shipyard)
        shipyardLoader.save(file, shipyardMap.values.toList())
        return shipyardMap[waypointId]!!
    }

    private fun loadLocal(forceReload : Boolean = false){
        if(loaded && !forceReload) return
        try {
            shipyardMap.clear()
            shipyardLoader.load(file).forEach {
                shipyardMap[it.shipyard.symbol] = it
            }
            loaded = true

        } catch (t : Throwable){
            logger.error("ShipyardRepo ${t.message}")
        }
    }
}