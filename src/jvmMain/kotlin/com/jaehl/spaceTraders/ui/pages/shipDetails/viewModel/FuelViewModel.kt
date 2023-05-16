package com.jaehl.spaceTraders.ui.pages.shipDetails.viewModel

import com.jaehl.spaceTraders.data.model.Ship

data class FuelViewModel(
    val capacity : Int,
    val current : Int,
    val enabled : Boolean,
) {
    companion object {
        fun create(ship: Ship) : FuelViewModel {
            return FuelViewModel(
                capacity = ship.fuel.capacity,
                current = ship.fuel.current,
                enabled = (ship.nav.status == Ship.Nav.Statue.Docked)
            )
        }
    }
}