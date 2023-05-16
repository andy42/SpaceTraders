package com.jaehl.spaceTraders.ui.pages.shipDetails.viewModel

import com.jaehl.spaceTraders.data.model.CargoData
import com.jaehl.spaceTraders.data.model.Ship
import com.jaehl.spaceTraders.data.repo.CargoInfoRepo

data class CargoViewModel(
    val capacity : Int,
    val units : Int,
    val inventory : List<CargoItemViewModel>
) {

    data class CargoItemViewModel(
        val symbol: String,
        val name: String,
        val description: String,
        val units: Int,
        val isRefinable: Boolean,
        val canJettison: Boolean,
        val icon : String
    ) {
        companion object {
            fun create(ship: Ship, inventoryItem: Ship.Cargo.InventoryItem, cargoData: CargoData, hasRefinery : Boolean): CargoItemViewModel {
                return CargoItemViewModel(
                    symbol = cargoData.symbol,
                    name = cargoData.name,
                    description = cargoData.description,
                    units = inventoryItem.units,
                    isRefinable = (cargoData.refineTo != null) && hasRefinery,
                    canJettison = (ship.nav.status == Ship.Nav.Statue.InOrbit),
                    icon = cargoData.icon
                )
            }
        }
    }

    companion object {
        fun create(ship: Ship, cargoInfoRepo : CargoInfoRepo) : CargoViewModel{
            val hasRefinery = ship.hasRefinery()
            return CargoViewModel(
                capacity = ship.cargo.capacity,
                units = ship.cargo.units,
                inventory = ship.cargo.inventory.map {
                    CargoViewModel.CargoItemViewModel.create(
                        ship = ship,
                        inventoryItem = it,
                        cargoData = cargoInfoRepo.getCargoData(it),
                        hasRefinery = hasRefinery
                    )
                }
            )
        }
    }
}