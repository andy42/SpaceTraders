package com.jaehl.spaceTraders.data.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Contract(
    val id : String = "",
    val factionSymbol : String = "",
    val type : ContractType = ContractType.Procurement,
    val terms : Terms = Terms(),
    val accepted : Boolean = false,
    val fulfilled : Boolean = false,
    val expiration : Date = Date()

) {
    enum class ContractType(val value : String) {
        @SerializedName("PROCUREMENT")
        Procurement("Procurement"),

        @SerializedName("TRANSPORT")
        Transport("Transport"),

        @SerializedName("SHUTTLE")
        Shuttle("Shuttle"),
    }

    data class Terms(
        val deadline : Date = Date(),
        val payment : Payment = Payment(),
        val deliver : List<Deliver> = listOf(),

    ) {
        data class Payment(
            val onAccepted : Int = 0,
            val onFulfilled : Int = 0
        )
        data class Deliver(
            val tradeSymbol : String = "",
            val destinationSymbol : String = "",
            val unitsRequired : Int = 0,
            val unitsFulfilled : Int = 0
        )
    }
}
