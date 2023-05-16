package com.jaehl.spaceTraders.data.model.response

import com.jaehl.spaceTraders.data.model.Contract
import com.jaehl.spaceTraders.data.model.Ship

data class ContractDeliverResponse(
    val contract : Contract = Contract(),
    val cargo: Ship.Cargo = Ship.Cargo()
)
