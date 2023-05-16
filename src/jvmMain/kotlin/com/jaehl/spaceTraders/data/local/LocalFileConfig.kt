package com.jaehl.spaceTraders.data.local

import java.io.File
import java.nio.file.Paths
import javax.inject.Inject
import kotlin.io.path.createDirectory
import kotlin.io.path.exists

interface LocalFileConfig {
    fun getCargoInfoFile() : File
    fun getMarketHistoryFile(waypointId: String) : File
    fun getSystemsFile() : File
    fun getShipyardFile() : File
    fun getAgentFile() : File
}

class LocalFileConfigImp @Inject constructor() : LocalFileConfig {

    private fun getProjectUserDir() : File {
        val directory = Paths.get(System.getProperty("user.home"), projectUserDir)
        if( !directory.exists()){
            directory.createDirectory()
        }
        return directory.toFile()
    }

    private fun getFile(fileName : String) : File {
        val directory = getProjectUserDir()
        return Paths.get(directory.absolutePath, fileName).toFile()
    }

    override fun getCargoInfoFile(): File {
        return getFile("cargoInfo.json")
    }

    override fun getMarketHistoryFile(waypointId: String): File {
        val projectUserDir = getProjectUserDir()
        val marketDirectory = Paths.get(projectUserDir.absolutePath, "marketDirectory")
        if( !marketDirectory.exists()){
            marketDirectory.createDirectory()
        }
        return Paths.get(marketDirectory.toFile().absolutePath, "${waypointId}.json").toFile()
    }

    override fun getSystemsFile(): File {
        return getFile("system.json")
    }

    override fun getShipyardFile(): File {
        return getFile("shipyard.json")
    }

    override fun getAgentFile(): File {
        return getFile("agents.json")
    }

    companion object {
        val projectUserDir = "SpaceTraders"
    }
}