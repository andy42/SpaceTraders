package com.jaehl.spaceTraders.data.model.request

data class ContractDeliverRequest(
    val shipSymbol : String,
    val tradeSymbol : String,
    val units : Int
)
