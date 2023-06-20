package com.jaehl.spaceTraders.data.model.response

import com.jaehl.spaceTraders.util.DateHelper
import com.jaehl.spaceTraders.util.DateHelperImp
import java.util.Date

data class Cooldown(
    val shipSymbol : String = "",
    val totalSeconds : Int = 0,
    val remainingSeconds : Int = 0,
    val expiration : Date = Date(0L)
) {
    fun isFinished(dateHelper : DateHelper = DateHelperImp()) : Boolean {
        return (expiration.time <= dateHelper.getNow().time)
    }
    fun getDelay(dateHelper : DateHelper = DateHelperImp()) : Long {
        return (expiration.time - dateHelper.getNow().time)
    }
}
