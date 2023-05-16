package com.jaehl.spaceTraders.ui.pages.shipDetails.viewModel

import com.jaehl.spaceTraders.data.model.CargoData

data class MiningViewModel(
    val isVisible : Boolean = false,
    val canMine : Boolean = false,
    val yield : Yield? = null
) {
    data class Yield(
        val cargoData: CargoData,
        val units : Int
    )
}