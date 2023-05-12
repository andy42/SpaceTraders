package com.jaehl.spaceTraders.data.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Transaction(
    val waypointSymbol : String = "",
    val shipSymbol : String = "",
    val tradeSymbol : String = "",
    val type : Type = Type.Purchase,
    val units : Int = 0,
    val pricePerUnit : Int = 0,
    val totalPrice : Int = 0,
    val timestamp : Date = Date()
) {
    enum class Type(val value : String) {
        @SerializedName("PURCHASE")
        Purchase("Purchase"),

        @SerializedName("SELL")
        Sell("Sell")
    }
}
