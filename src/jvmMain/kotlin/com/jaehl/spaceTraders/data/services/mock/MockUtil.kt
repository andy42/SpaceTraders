package com.jaehl.spaceTraders.data.services.mock

import com.jaehl.spaceTraders.data.model.Ship

object MockUtil {
    fun removeCargo(cargo : Ship.Cargo, cargoId : String, units : Int) : Ship.Cargo{
        val newInventory = cargo.inventory.toMutableList()

        if(newInventory.firstOrNull {it.symbol == cargoId} == null) throw Exception("does not contain cargo $cargoId")

        val index = newInventory.indexOfFirst { it.symbol == cargoId }
        if((newInventory[index].units - units) < 0) throw Exception("can not remove more cargo than there is for $cargoId")
        if((newInventory[index].units - units) == 0) {
            newInventory.removeAt(index)
        }
        else {
            newInventory[index] = newInventory[index].copy(
                units = newInventory[index].units - units
            )
        }

        return cargo.copy(
            inventory = newInventory,
            units = cargo.units - units
        )
    }

    fun addCargo(cargo : Ship.Cargo, cargoId : String, units : Int) : Ship.Cargo{
        val newInventory = cargo.inventory.toMutableList()
        if((cargo.units + units) > cargo.capacity) throw Exception("can not add cargo over capacity")
        else if(newInventory.firstOrNull {it.symbol == cargoId} != null){
            val index = newInventory.indexOfFirst { it.symbol == cargoId }
            newInventory[index] = newInventory[index].copy(
                units = newInventory[index].units + units
            )
        } else {
            newInventory.add(
                Ship.Cargo.InventoryItem(
                    symbol = cargoId,
                    name = cargoId,
                    description = cargoId,
                    units = units
                )
            )
        }
        return cargo.copy(
            inventory = newInventory,
            units = cargo.units + units
        )
    }
}