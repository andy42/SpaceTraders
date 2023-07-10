package com.jaehl.spaceTraders.data.repo.mock

import com.jaehl.spaceTraders.data.model.StarSystem
import com.jaehl.spaceTraders.data.repo.SystemRepo

class SystemRepoMock : SystemRepo {

    private val starSystemMap = LinkedHashMap<String, StarSystem>()

    fun addStarSystem(starSystem : StarSystem){
        starSystemMap[starSystem.symbol] = starSystem
    }

    override fun getStarSystems(): List<StarSystem> {
        return starSystemMap.values.toList()
    }

    override fun getStarSystem(systemId: String): StarSystem? {
        return starSystemMap[systemId]
    }

    override suspend fun deapLoadAll() {

    }

    fun clear() {
        starSystemMap.clear()
    }
}