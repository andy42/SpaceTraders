package com.jaehl.spaceTraders.data.model.response

import com.jaehl.spaceTraders.data.model.Ship

data class RefineMaterialsResponse(
    val cargo : Ship.Cargo = Ship.Cargo(),
    val cooldown: Cooldown = Cooldown(),
    val produced : List<Amount> = listOf(),
    val consumed : List<Amount> = listOf(),
) {
    data class Amount(
        val tradeSymbol : String = "",
        val units : Int = 0
    )
}
