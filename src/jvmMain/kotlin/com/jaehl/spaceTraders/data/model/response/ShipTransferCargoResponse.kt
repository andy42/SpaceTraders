package com.jaehl.spaceTraders.data.model.response

import com.jaehl.spaceTraders.data.model.Ship

data class ShipTransferCargoResponse(
    val cargo : Ship.Cargo = Ship.Cargo()
)
