package com.jaehl.spaceTraders.ui.pages.shipDetails.tasks

import com.jaehl.spaceTraders.data.model.Ship
import com.jaehl.spaceTraders.data.model.response.Cooldown

interface ShipState {
    var ship : Ship
    var coolDown : Cooldown
}