package com.jaehl.spaceTraders.ui.pages.shipDetails.viewModel

import com.jaehl.spaceTraders.data.model.Ship
import com.jaehl.spaceTraders.data.model.WaypointType
import java.util.Date

data class NavViewModel(
    val location : String,
    val locationType : String,
    val arrival : Date,
    val enabled : Boolean,
    val waypointType : WaypointType
) {
    companion object {
        fun create(ship: Ship) : NavViewModel {
            return NavViewModel(
                location = ship.nav.route.destination.symbol,
                locationType = ship.nav.route.destination.type.value,
                arrival = ship.nav.route.arrival,
                enabled = (ship.nav.status == Ship.Nav.Statue.InOrbit),
                waypointType = ship.nav.route.destination.type
            )
        }
    }
}