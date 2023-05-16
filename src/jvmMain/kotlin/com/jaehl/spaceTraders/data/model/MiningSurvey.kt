package com.jaehl.spaceTraders.data.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class MiningSurvey(
    val signature : String = "",
    val symbol : String = "",
    val deposits : List<Symbol> = listOf(),
    val expiration : Date = Date(),
    val size :DepositSize = DepositSize.Small
) {
    enum class DepositSize(val value : String) {
        @SerializedName("SMALL")
        Small("Small"),

        @SerializedName("MODERATE")
        Moderate("Moderate"),

        @SerializedName("LARGE")
        Large("Large"),
    }

    fun isExpired() : Boolean {
        return expiration.time < Date().time
    }

}
