package com.jaehl.spaceTraders.data.model.response

import com.jaehl.spaceTraders.data.model.Agent
import com.jaehl.spaceTraders.data.model.Contract

data class ContractFulfillResponse(
    val agent : Agent = Agent(),
    val contract : Contract = Contract()
)
