package com.jaehl.spaceTraders.data.model

data class CargoData(
    val symbol : String = "",
    val name : String = "",
    val description : String = "",
    val refineTo : String? = null
) {
    companion object {
        fun create(inventoryItem : Ship.Cargo.InventoryItem) : CargoData {
            return CargoData(
                symbol = inventoryItem.symbol,
                name = inventoryItem.name,
                description = inventoryItem.description
            )
        }
    }
}