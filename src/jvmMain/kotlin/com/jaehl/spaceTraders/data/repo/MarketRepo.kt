package com.jaehl.spaceTraders.data.repo

import com.jaehl.spaceTraders.data.local.LocalFileConfig
import com.jaehl.spaceTraders.data.local.ObjectLoader
import com.jaehl.spaceTraders.data.model.MarketHistory
import com.jaehl.spaceTraders.data.services.SystemService
import java.io.File
import java.util.*
import javax.inject.Inject

class MarketRepo @Inject constructor(
    private val marketHistoryLoader : ObjectLoader<MarketHistory>,
    private val localFileConfig : LocalFileConfig,
    private val systemService: SystemService
) {
    fun updateMarket(systemId: String, waypointId: String) : MarketHistory? {
        val response = systemService.getMarket(systemId, waypointId)
        val marketHistory = marketHistoryLoader.load(localFileConfig.getMarketHistoryFile(waypointId))

        if(marketHistory == null){
            val newMarketHistory = MarketHistory.create(
                date = Date(),
                market = response,
                detailedInfo = response.tradeGoods.isNotEmpty()
            )
            marketHistoryLoader.save(localFileConfig.getMarketHistoryFile(waypointId),newMarketHistory)
            return newMarketHistory
        } else {
            val tradeHistory = marketHistory.tradeHistory.toMutableList()

            if(response.tradeGoods.isNotEmpty()) {
                if(tradeHistory.isEmpty()) {
                    tradeHistory.add(0, MarketHistory.TradeHistory.create(Date(), response))
                }
                else if((Date().time - historyIntervals) > tradeHistory.first().date.time ) {
                    tradeHistory.add(0, MarketHistory.TradeHistory.create(Date(), response))
                }
            }

            val newMarketHistory = marketHistory.copy(
                lastUpdate = Date(),
                detailedInfo = (marketHistory.detailedInfo || response.tradeGoods.isNotEmpty()),
                imports = response.imports,
                exports = response.exports,
                exchange = response.exchange,
                transactions = if(response.transactions.isNotEmpty()) marketHistory.transactions else response.transactions,
                tradeGoods = if(response.tradeGoods.isNotEmpty()) marketHistory.tradeGoods else response.tradeGoods,
                tradeHistory = tradeHistory
            )
            marketHistoryLoader.save(localFileConfig.getMarketHistoryFile(waypointId),newMarketHistory)
            return newMarketHistory
        }
    }

    fun getMarketHistory(systemId: String, waypointId: String) : MarketHistory? {
        return marketHistoryLoader.load(localFileConfig.getMarketHistoryFile(waypointId))
    }

    val historyIntervals = 1000*60*60
}