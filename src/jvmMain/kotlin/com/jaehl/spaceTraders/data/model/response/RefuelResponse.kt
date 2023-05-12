package com.jaehl.spaceTraders.data.model.response

import com.jaehl.spaceTraders.data.model.Agent
import com.jaehl.spaceTraders.data.model.Ship

data class RefuelResponse(
    val agent : Agent,
    val fuel : Ship.Fuel
)
