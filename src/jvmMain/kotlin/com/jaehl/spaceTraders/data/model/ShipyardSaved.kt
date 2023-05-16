package com.jaehl.spaceTraders.data.model

import java.util.Date

data class ShipyardSaved(
    val date : Date?,
    val shipyard: Shipyard
) {
    companion object {
        fun create(shipyard : Shipyard) : ShipyardSaved{
            return ShipyardSaved(
                date = if(shipyard.ships.isEmpty()) null else Date(),
                shipyard = shipyard
            )
        }
    }
}
