package com.jaehl.spaceTraders.data.model.response

import com.jaehl.spaceTraders.data.model.StarSystem

data class JumpGateResponse(
    val jumpRange : Int = 0,
    val factionSymbol : String = "",
    val connectedSystems : List<ConnectedSystem> = listOf()
) {
    data class ConnectedSystem(
        val symbol : String = "",
        val sectorSymbol : String = "",
        val type : StarSystem.SystemType,
        val factionSymbol : String = "",
        val x : Int = 0,
        val y : Int = 0,
        val distance : Int = 0
    )
}
