package com.jaehl.spaceTraders.data.model

data class Market(
    val symbol : String = "",
    val imports : List<SymbolDetails> = listOf(),
    val exports : List<SymbolDetails> = listOf(),
    val exchange : List<SymbolDetails> = listOf(),
    val transactions : List<Transaction> = listOf(),
    val tradeGoods : List<TradeGood> = listOf()
)