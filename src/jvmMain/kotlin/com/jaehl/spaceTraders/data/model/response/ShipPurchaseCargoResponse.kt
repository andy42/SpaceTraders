package com.jaehl.spaceTraders.data.model.response

import com.jaehl.spaceTraders.data.model.Agent
import com.jaehl.spaceTraders.data.model.Ship
import com.jaehl.spaceTraders.data.model.Transaction

data class ShipPurchaseCargoResponse(
    val agent : Agent,
    val cargo : Ship.Cargo,
    val transaction : Transaction
)
