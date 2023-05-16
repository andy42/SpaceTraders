package com.jaehl.spaceTraders.data.model.request

import com.jaehl.spaceTraders.data.model.FactionName

data class RegisterRequest(
    val faction : FactionName,
    val symbol : String,
    val email : String
)