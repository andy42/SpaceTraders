package com.jaehl.spaceTraders.data.model

import java.util.Date

data class MarketHistory(
    val symbol : String = "",
    val lastUpdate : Date = Date(),
    val detailedInfo : Boolean = false,

    val imports : List<SymbolDetails> = listOf(),
    val exports : List<SymbolDetails> = listOf(),
    val exchange : List<SymbolDetails> = listOf(),

    val tradeHistory : List<TradeHistory> = listOf()
) {

    fun getLatestTradeGood(cargoId : String) : TradeGood? {
        return tradeHistory.first().tradeGoods.firstOrNull {it.symbol == cargoId}
    }

    data class TradeHistory(
        val date : Date = Date(),
        val transactions : List<Transaction> = listOf(),
        val tradeGoods : List<TradeGood> = listOf()
    ) {
        companion object {
            fun create(date : Date, market : Market) : TradeHistory{
                return TradeHistory(
                    date = date,
                    transactions = market.transactions,
                    tradeGoods = market.tradeGoods
                )
            }
        }
    }
    companion object {
        fun create(date : Date, market : Market, detailedInfo : Boolean) : MarketHistory {
            return MarketHistory(
                symbol = market.symbol,
                lastUpdate = date,
                detailedInfo = detailedInfo,
                imports = market.imports,
                exports = market.exports,
                exchange = market.exchange,
                tradeHistory = if(detailedInfo) listOf(TradeHistory.create(date, market)) else listOf<TradeHistory>()
            )
        }
    }
}
