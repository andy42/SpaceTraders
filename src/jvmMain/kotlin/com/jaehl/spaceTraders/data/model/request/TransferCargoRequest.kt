package com.jaehl.spaceTraders.data.model.request

data class TransferCargoRequest(
    val tradeSymbol : String,
    val units : Int,
    val shipSymbol : String
)
