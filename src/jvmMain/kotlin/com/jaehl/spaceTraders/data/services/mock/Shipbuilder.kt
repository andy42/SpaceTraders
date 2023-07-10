package com.jaehl.spaceTraders.data.services.mock

import com.jaehl.spaceTraders.data.model.Ship
import com.jaehl.spaceTraders.util.DateHelper

class Shipbuilder(
    private val dateHelper: DateHelper
) {

    private var symbol = ""
    private var nav = Ship.Nav(
        route = Ship.Nav.NavRoute(
            arrival = dateHelper.getNow()
        )
    )
    private var cargo = Ship.Cargo()

    private var fuel = Ship.Fuel(
        current = 100,
        capacity = 100
    )

    fun setSymbol(symbol : String) : Shipbuilder {
        this.symbol = symbol
        return this
    }

    fun setNavStatus(status : Ship.Nav.Statue) : Shipbuilder {
        this.nav = nav.copy(
            status = status
        )
        return this
    }

    fun setNav(nav : Ship.Nav) : Shipbuilder {
        this.nav = nav
        return this
    }

    fun setFuel(fuel : Ship.Fuel) : Shipbuilder {
        this.fuel = fuel
        return this
    }

    fun setCargo(cargo : Ship.Cargo) : Shipbuilder {
        this.cargo = cargo
        return this
    }

    fun setCargo(capacity : Int, inventory : List<Ship.Cargo.InventoryItem>) : Shipbuilder {
        var cargoAmount = 0
        inventory.forEach {
            cargoAmount += it.units
        }
        cargo = Ship.Cargo(
            capacity = capacity,
            units = cargoAmount,
            inventory = inventory
        )
        return this
    }

    fun build() : Ship {
        return Ship(
            symbol = symbol,
            nav = nav,
            cargo = cargo,
            fuel = fuel
        )
    }
}