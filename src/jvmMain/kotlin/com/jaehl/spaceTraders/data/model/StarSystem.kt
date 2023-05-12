package com.jaehl.spaceTraders.data.model

import com.google.gson.annotations.SerializedName

data class StarSystem(
    val symbol : String = "",
    val sectorSymbol : String = "",
    val type : SystemType,
    val waypoints : List<SystemWaypoint> = listOf()
) {
    enum class SystemType(val value : String) {
        @SerializedName("NEUTRON_STAR")
        NeutronStar("Neutron Star"),

        @SerializedName("RED_STAR")
        RedStar("Red Star"),

        @SerializedName("ORANGE_STAR")
        OrangeStar("Orange Star"),

        @SerializedName("BLUE_STAR")
        BlueStar("Blue Star"),

        @SerializedName("YOUNG_STAR")
        YoungStar("Young Star"),

        @SerializedName("WHITE_DWARF")
        WhiteDwarf("White Dwarf"),

        @SerializedName("BLACK_HOLE")
        BlackHole("Black Hole"),

        @SerializedName("HYPERGIANT")
        Hypergiant("Hypergiant"),

        @SerializedName("NEBULA")
        Nebula("Nebula"),

        @SerializedName("UNSTABLE")
        Unstable("Unstable")


    }
}
