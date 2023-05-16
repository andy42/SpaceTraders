package com.jaehl.spaceTraders.ui.pages.shipDetails.viewModel

import com.jaehl.spaceTraders.data.model.Ship
import com.jaehl.spaceTraders.data.repo.CargoInfoRepo

data class ShipViewModel(
    val shipId : String,
    val name : String,
    val state : StatusViewModel,
    val nav : NavViewModel,
    val fuel : FuelViewModel,
    val cargo : CargoViewModel
) {
    companion object {
        fun create(ship: Ship, cargoInfoRepo : CargoInfoRepo) : ShipViewModel {
            return ShipViewModel(
                shipId = ship.symbol,
                name = ship.symbol,
                state = StatusViewModel.create(ship),
                nav = NavViewModel.create(ship),
                fuel = FuelViewModel.create(ship),
                cargo = CargoViewModel.create(ship, cargoInfoRepo)
            )
        }
    }
}
