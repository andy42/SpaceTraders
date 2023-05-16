package com.jaehl.spaceTraders.data.model.response

import com.jaehl.spaceTraders.data.model.Agent

data class RegisterResponse(
    val token : String = "",
    val agent : Agent = Agent()
)
