package com.jaehl.spaceTraders.data.repo.mock

import com.jaehl.spaceTraders.data.model.MarketHistory
import com.jaehl.spaceTraders.data.repo.MarketRepo
import com.jaehl.spaceTraders.util.DateHelper

class MarketRepoMock(
    private val dateHelper: DateHelper
) : MarketRepo {

    private val marketHistoryMap = HashMap<String, MarketHistory>()

    fun addMarketHistory(marketHistory : MarketHistory) {
        marketHistoryMap[marketHistory.symbol] = marketHistory
    }

    override fun updateMarket(systemId: String, waypointId: String): MarketHistory? {
        var marketHistory = marketHistoryMap[waypointId]?.copy(
            lastUpdate = dateHelper.getNow()
        ) ?: return null
        marketHistoryMap[waypointId] = marketHistory
        return marketHistory
    }

    override fun getMarketHistory(systemId: String, waypointId: String): MarketHistory? {
        return marketHistoryMap[waypointId]
    }

    fun clear() {
        marketHistoryMap.clear()
    }
}