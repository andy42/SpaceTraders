package com.jaehl.spaceTraders.data.model

import com.google.gson.annotations.SerializedName

enum class FactionName (val value : String) {
    @SerializedName("COSMIC")
    Cosmic("Cosmic"),

    @SerializedName("VOID")
    Void("Void"),

    @SerializedName("GALACTIC")
    Galactic("Galactic"),

    @SerializedName("QUANTUM")
    Quantum("Quantum"),

    @SerializedName("DOMINION")
    Dominion("Dominion"),
}