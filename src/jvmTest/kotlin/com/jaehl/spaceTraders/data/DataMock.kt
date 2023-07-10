package com.jaehl.spaceTraders.data

import com.jaehl.spaceTraders.data.model.*
import com.jaehl.spaceTraders.data.model.response.Cooldown
import com.jaehl.spaceTraders.data.model.response.ExtractResponse
import com.jaehl.spaceTraders.data.repo.mock.MarketRepoMock
import com.jaehl.spaceTraders.data.repo.mock.SystemRepoMock
import com.jaehl.spaceTraders.data.services.mock.ContractServiceMock
import com.jaehl.spaceTraders.data.services.mock.FleetServiceMock
import com.jaehl.spaceTraders.util.DateHelperMock
import java.util.ArrayList

class DataMock {

    private var agent = Agent()
    private val shipMap = HashMap<String, Ship>()
    private val transActions = ArrayList<Transaction>()
    private val shipCoolDowns = HashMap<String, Cooldown?>()
    private val refineDataMap = HashMap<String, FleetServiceMock.RefineData>()
    private val contractMap = HashMap<String, Contract>()

    val dateHelper = DateHelperMock()

    val systemRepo = SystemRepoMock()
    val marketRepo = MarketRepoMock(dateHelper)

    val fleetService = FleetServiceMock(
        agent = agent,
        shipMap = shipMap,
        transActions = transActions,
        shipCoolDowns = shipCoolDowns,
        refineDataMap = refineDataMap,
        dateHelper = dateHelper
    )

    val contractService = ContractServiceMock(
        agent = agent,
        shipMap = shipMap,
        contractMap = contractMap
    )

    fun clear() {
        agent = Agent()
        shipMap.clear()
        shipCoolDowns.clear()
        refineDataMap.clear()
        contractMap.clear()
        marketRepo.clear()
    }

    fun getShip(shipId : String) : Ship?{
        return shipMap[shipId]
    }

    fun getTransActions() : List<Transaction> = transActions

    fun getShipCoolDown(shipId : String) : Cooldown? {
        return shipCoolDowns[shipId]
    }

    fun addShip(ship : Ship) : Ship{
        shipMap[ship.symbol] = ship
        return ship
    }

    fun createAndAddMarketHistory(waypointId : String, tradeGoods : List<TradeGood>) {
        marketRepo.addMarketHistory(
            MarketHistory(
                symbol = waypointId,
                lastUpdate = dateHelper.getNow(),
                detailedInfo = true,
                tradeGoods = tradeGoods
            )
        )
    }

    fun getMarketHistory(systemId : String, waypointId : String) : MarketHistory? {
        return marketRepo.getMarketHistory(systemId = systemId, waypointId = waypointId)
    }

    fun createAsteroidFieldWaypoint(waypointId : String, withMarket : Boolean) : SystemWaypoint{
        return SystemWaypoint(
            symbol = waypointId,
            type = WaypointType.asteroidField,
            traits = if(withMarket) listOf(WaypointTrait(symbol = TraitType.Marketplace)) else listOf()
        )
    }

    fun createPlanetWaypoint(waypointId : String, withMarket : Boolean) : SystemWaypoint{
        return SystemWaypoint(
            symbol = waypointId,
            type = WaypointType.planet,
            traits = if(withMarket) listOf(WaypointTrait(symbol = TraitType.Marketplace)) else listOf()
        )
    }

    fun setExtractYield(yield : ExtractResponse.Yield) {
        fleetService.extractYield = yield
    }

    fun addStarSystem(starSystem : StarSystem) {
        systemRepo.addStarSystem(starSystem)
    }
}