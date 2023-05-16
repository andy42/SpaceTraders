package com.jaehl.spaceTraders.data.model.response

import com.jaehl.spaceTraders.data.model.Ship

data class ShipWarpResponse(
    val fuel : Ship.Fuel = Ship.Fuel(),
    val nav : Ship.Nav = Ship.Nav()
)
