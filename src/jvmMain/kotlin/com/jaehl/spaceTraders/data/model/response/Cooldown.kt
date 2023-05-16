package com.jaehl.spaceTraders.data.model.response

import java.util.Date

data class Cooldown(
    val shipSymbol : String = "",
    val totalSeconds : Int = 0,
    val remainingSeconds : Int = 0,
    val expiration : Date = Date()
) {
    fun isFinished() : Boolean {
        return (expiration.time <= Date().time)
    }
    fun getDelay() : Long {
        return (expiration.time - Date().time)
    }
}
