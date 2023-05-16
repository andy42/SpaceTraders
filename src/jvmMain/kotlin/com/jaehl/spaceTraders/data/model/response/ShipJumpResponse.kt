package com.jaehl.spaceTraders.data.model.response

import com.jaehl.spaceTraders.data.model.Ship

data class ShipJumpResponse(
    val cooldown : Cooldown = Cooldown(),
    val nav : Ship.Nav = Ship.Nav()
)
