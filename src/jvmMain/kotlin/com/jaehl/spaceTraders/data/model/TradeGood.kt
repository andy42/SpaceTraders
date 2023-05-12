package com.jaehl.spaceTraders.data.model

import com.google.gson.annotations.SerializedName

data class TradeGood(
    val symbol : String = "",
    val tradeVolume : Int = 0,
    val supply : Supply = Supply.Scarce,
    val purchasePrice : Int = 0,
    val sellPrice : Int = 0,
) {
    enum class Supply(val value : String) {

        @SerializedName("SCARCE")
        Scarce("Scarce"),

        @SerializedName("LIMITED")
        Limited("Limited"),

        @SerializedName("MODERATE")
        Moderate("Moderate"),

        @SerializedName("ABUNDANT")
        Abundant("Abundant")
    }
}
