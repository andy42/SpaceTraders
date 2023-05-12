package com.jaehl.spaceTraders.data.model.response

import com.jaehl.spaceTraders.data.model.Ship

data class ExtractResponse(
    val extraction : Extraction = Extraction(),
    val cargo : Ship.Cargo = Ship.Cargo(),
    val cooldown: Cooldown = Cooldown(),
) {
    data class Extraction(
        val shipSymbol : String = "",
        val yield : Yield = Yield()
    )
    data class Yield(
        val symbol : String = "",
        val units : Int = 0
    )
}
