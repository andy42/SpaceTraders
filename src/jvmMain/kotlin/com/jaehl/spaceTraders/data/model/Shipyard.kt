package com.jaehl.spaceTraders.data.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Shipyard(
    val symbol : String = "",
    val shipTypes : List<ShipType> = listOf(),
    val transactions : List<ShipTransaction> = listOf(),
    val ships : List<ShipyardShip> = listOf()

) {
    data class ShipType(
        val type : ShipTypes = ShipTypes.ShipExplorer
    )
    enum class ShipTypes(val value : String){
        @SerializedName("SHIP_PROBE")
        ShipProbe("Ship Probe"),

        @SerializedName("SHIP_MINING_DRONE")
        ShipMiningDrone("Ship MiningDrone"),

        @SerializedName("SHIP_INTERCEPTOR")
        ShipInterceptor("Ship Interceptor"),

        @SerializedName("SHIP_LIGHT_HAULER")
        ShipLightHauler("Ship LightHauler"),

        @SerializedName("SHIP_COMMAND_FRIGATE")
        ShipCommandFrigate("Ship CommandFrigate"),

        @SerializedName("SHIP_EXPLORER")
        ShipExplorer("Ship Explorer"),

        @SerializedName("SHIP_HEAVY_FREIGHTER")
        ShipHeavyFreighter("Ship HeavyFreighter"),

        @SerializedName("SHIP_LIGHT_SHUTTLE")
        ShipLightShuttle("Ship LightShuttle"),

        @SerializedName("SHIP_ORE_HOUND")
        ShipOreHound("Ship Ore Hound"),

        @SerializedName("SHIP_REFINING_FREIGHTER")
        ShipRefiningFreighter("Ship Refining Freighter")
    }

    enum class ShipTransaction(
        val waypointSymbol : String = "",
        val shipSymbol : String = "",
        val price : Int = 0,
        val agentSymbol : String,
        val timestamp : Date = Date()

    )
    data class ShipyardShip(
        val type : ShipTypes = ShipTypes.ShipExplorer,
        val name : String = "",
        val description : String = "",
        val purchasePrice : Int = 0,
        val frame : Ship.Frame = Ship.Frame(),
        val reactor : Ship.Reactor = Ship.Reactor(),
        val engine : Ship.Engine = Ship.Engine(),
        val modules : List<Ship.ShipModule> = listOf(),
        val mounts : List<Ship.ShipMount> = listOf()
    )
}