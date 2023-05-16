package com.jaehl.spaceTraders.data.model.response

import com.jaehl.spaceTraders.data.model.Agent
import com.jaehl.spaceTraders.data.model.Contract

data class ContractAcceptResponse(
    val agent : Agent = Agent(),
    val contract : Contract = Contract()
)
