package com.jaehl.spaceTraders.ui.component.ship

import com.jaehl.spaceTraders.data.model.Ship
import java.util.Date

data class ShipViewModel(
    val shipId : String,
    val name : String,
    val state : String,
    val fuel : String,
    val cargo : String,
    val coolDownTime : Date? = null
) {
    companion object {
        fun create(ship: Ship) : ShipViewModel {
            return ShipViewModel(
                shipId = ship.symbol,
                name = ship.symbol,
                state = ship.nav.status.name,
                fuel = "Fuel (${ship.fuel.current} : ${ship.fuel.capacity})",
                cargo = "Cargo (${ship.cargo.units} : ${ship.cargo.capacity})"
            )
        }
    }
}