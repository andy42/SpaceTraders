package com.jaehl.spaceTraders.data.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Ship(
    val symbol : String = "",
    val registration : Registration = Registration(),
    val nav : Nav = Nav(),
    val crew : Crew = Crew(),
    val frame : Frame = Frame(),
    val reactor : Reactor = Reactor(),
    val engine : Engine = Engine(),
    val modules : List<ShipModule> = listOf(),
    val mounts : List<ShipMount> = listOf(),
    val cargo : Cargo = Cargo(),
    val fuel : Fuel = Fuel()
) {

    fun hasRefinery() : Boolean {
        return (modules.find {
            it.symbol == ShipModule.ModuleSymbol.ModuleOreRefinery1
        } != null)
    }

    fun hasMiningLaser() : Boolean {
        return (mounts.find {
            it.symbol == ShipMount.MountSymbol.MountMiningLaser1 ||
            it.symbol == ShipMount.MountSymbol.MountMiningLaser2 ||
            it.symbol == ShipMount.MountSymbol.MountMiningLaser3
        } != null)
    }

    fun hasSurveyor() : Boolean {
        return (mounts.find {
            it.symbol == ShipMount.MountSymbol.MountSurveyor1 ||
                    it.symbol == ShipMount.MountSymbol.MountSurveyor2 ||
                    it.symbol == ShipMount.MountSymbol.MountSurveyor3
        } != null)
    }

    fun hasGasSiphon() : Boolean {
        return (mounts.find {
            it.symbol == ShipMount.MountSymbol.MountGasSiphon1 ||
                    it.symbol == ShipMount.MountSymbol.MountGasSiphon2 ||
                    it.symbol == ShipMount.MountSymbol.MountGasSiphon3
        } != null)
    }

    fun getPosition() : Vector2d {
        return nav.route.destination.getPosition()
    }

    data class Registration(
        val name : String = "",
        val factionSymbol : String = "",
        val role : Role = Role.Fabricator
    )

    enum class Role(val value : String) {
        @SerializedName("FABRICATOR")
        Fabricator("Fabricator"),

        @SerializedName("HARVESTER")
        Harvester("Harvester"),

        @SerializedName("HAULER")
        Hauler("Hauler"),

        @SerializedName("INTERCEPTOR")
        Interceptor("Interceptor"),

        @SerializedName("EXCAVATOR")
        Excavator("Excavator"),

        @SerializedName("TRANSPORT")
        Transport("Transport"),

        @SerializedName("REPAIR")
        Repair("Repair"),

        @SerializedName("SURVEYOR")
        Surveyor("Surveyor"),

        @SerializedName("COMMAND")
        Command("Command"),

        @SerializedName("CARRIER")
        Carrier("Carrier"),

        @SerializedName("PATROL")
        Patrol("Patrol"),

        @SerializedName("SATELLITE")
        Satellite("Satellite"),

        @SerializedName("EXPLORER")
        Explorer("Explorer"),

        @SerializedName("REFINERY")
        Refinery("Refinery")
    }

    data class Nav(
        val systemSymbol : String = "",
        val waypointSymbol : String = "",
        val route : NavRoute = NavRoute(),
        val status : Statue = Statue.Docked,
        val flightMode : FlightMode = FlightMode.Cruise
    ) {
        enum class Statue(val value : String){
            @SerializedName("IN_TRANSIT")
            InTransit("InTransit"),

            @SerializedName("IN_ORBIT")
            InOrbit("InOrbit"),

            @SerializedName("DOCKED")
            Docked("Docked"),
        }
        enum class FlightMode(value : String){
            @SerializedName("DRIFT")
            Drift("Drift"),

            @SerializedName("STEALTH")
            Stealth("Stealth"),

            @SerializedName("CRUISE")
            Cruise("Cruise"),

            @SerializedName("BURN")
            Burn("Burn"),

            @SerializedName("DOCKED")
            Docked("Docked"),
        }

        data class NavRoute(
            val departure : SystemWaypoint = SystemWaypoint(),
            val destination : SystemWaypoint = SystemWaypoint(),
            val arrival : Date = Date(),
            val departureTime : Date = Date(),
        )
    }

    data class Crew(
        val current : Int = 0,
        val required : Int = 0,
        val capacity : Int = 0,
        val rotation : String = "",
        val morale : Int = 0,
        val wages : Int = 0
    )

    data class Frame(
        val symbol : Shipyard.ShipTypes = Shipyard.ShipTypes.ShipExplorer,
        val name : String = "",
        val description : String = "",
        val condition : Int = 0,
        val moduleSlots : Int = 0,
        val mountingPoints : Int = 0,
        val fuelCapacity : Int = 0,
        val requirements : Requirements = Requirements()
    )

    data class Reactor(
        val symbol : String = "",
        val name : String = "",
        val description : String = "",
        val condition : Int = 0,
        val powerOutput : Int = 0,
        val requirements : Requirements = Requirements()
    )

    data class Engine(
        val symbol : String = "",
        val name : String = "",
        val description : String = "",
        val condition : Int = 0,
        val speed : Int = 0,
        val requirements : Requirements = Requirements()
    )

    data class Fuel(
        val current : Int = 0,
        val capacity : Int = 0,
        val consumed : Consumed = Consumed()
    ) {
        data class Consumed(
            val amount : Int = 0,
            val timestamp : String = ""
        )
    }

    data class Cargo(
        val capacity : Int = 0,
        val units : Int = 0,
        val inventory : List<InventoryItem> = listOf()
    ) {
        data class InventoryItem(
            val symbol : String = "",
            val name : String = "",
            val description : String = "",
            val units : Int = 0
        )
        fun isFull() : Boolean {
            return units >= capacity
        }
    }

    data class ShipModule(
        val symbol : ModuleSymbol = ModuleSymbol.ModuleCargoHold1,
        val name : String = "",
        val description : String = "",
        val capacity : Int = 0,
        val requirements : Requirements = Requirements()

    ) {


        enum class ModuleSymbol(val value : String){
            @SerializedName("MODULE_MINERAL_PROCESSOR_I")
            ModuleMineralProcessor1("Module Mineral Processor 1"),

            @SerializedName("MODULE_CARGO_HOLD_I")
            ModuleCargoHold1("Module Cargo Hold 1"),

            @SerializedName("MODULE_CREW_QUARTERS_I")
            ModuleCrewQuarters1("Module Crew Quarters 1"),

            @SerializedName("MODULE_ENVOY_QUARTERS_I")
            ModuleEnvoyQuarters1("Module Envoy Quarters 1"),

            @SerializedName("MODULE_PASSENGER_CABIN_I")
            ModulePassengerCabin1("Module Passenger Cabin 1"),

            @SerializedName("MODULE_MICRO_REFINERY_I")
            ModuleMicroRefinery1("Module Micro Refinery 1"),

            @SerializedName("MODULE_ORE_REFINERY_I")
            ModuleOreRefinery1("Module Ore Refinery 1"),

            @SerializedName("MODULE_FUEL_REFINERY_I")
            ModuleFuelRefinery1("Module Fuel Refinery 1"),

            @SerializedName("MODULE_SCIENCE_LAB_I")
            ModuleScienceLab1("Module Science Lab 1"),

            @SerializedName("MODULE_JUMP_DRIVE_I")
            ModuleJumpDrive1("Module Jump Drive 1"),

            @SerializedName("MODULE_JUMP_DRIVE_II")
            ModuleJumpDrive2("Module Jump Drive 2"),

            @SerializedName("MODULE_JUMP_DRIVE_III")
            ModuleJumpDrive3("Module Jump Drive 3"),

            @SerializedName("MODULE_WARP_DRIVE_I")
            ModuleWarpDrive1("Module Warp Drive 1"),

            @SerializedName("MODULE_WARP_DRIVE_II")
            ModuleWarpDrive2("Module Warp Drive 2"),

            @SerializedName("MODULE_WARP_DRIVE_III")
            ModuleWarpDrive3("Module Warp Drive 3"),

            @SerializedName("MODULE_SHIELD_GENERATOR_I")
            ModuleShieldGenerator1("Module Shield Generator 1"),

            @SerializedName("MODULE_SHIELD_GENERATOR_II")
            ModuleShieldGenerator2("Module Shield Generator 2")
        }
    }

    data class Requirements(
        val crew : Int = 0,
        val power : Int = 0,
        val slots : Int = 0
    )

    data class ShipMount(
        val symbol : MountSymbol = MountSymbol.MountSurveyor1,
        val name : String = "",
        val description : String = "",
        val strength : Int = 0,
        val deposits : List<String> = listOf(),
        val requirements : Requirements = Requirements()

    ) {
        data class Requirements(
            val crew: Int = 0,
            val power: Int = 0,
            val slots: Int = 0
        )

        enum class MountSymbol(val value : String){
            @SerializedName("MOUNT_GAS_SIPHON_I")
            MountGasSiphon1("Mount Gas Siphon 1"),

            @SerializedName("MOUNT_GAS_SIPHON_II")
            MountGasSiphon2("Mount Gas Siphon 2"),

            @SerializedName("MOUNT_GAS_SIPHON_III")
            MountGasSiphon3("Mount Gas Siphon 3"),

            @SerializedName("MOUNT_SURVEYOR_I")
            MountSurveyor1("Mount Surveyor 1"),

            @SerializedName("MOUNT_SURVEYOR_II")
            MountSurveyor2("Mount Surveyor 2"),

            @SerializedName("MOUNT_SURVEYOR_III")
            MountSurveyor3("Mount Surveyor 3"),

            @SerializedName("MOUNT_SENSOR_ARRAY_I")
            MountSensorArray1("Mount Sensor Array 1"),

            @SerializedName("MOUNT_SENSOR_ARRAY_II")
            MountSensorArray2("Mount Sensor Array 2"),

            @SerializedName("MOUNT_SENSOR_ARRAY_III")
            MountSensorArray3("Mount Sensor Array 3"),

            @SerializedName("MOUNT_MINING_LASER_I")
            MountMiningLaser1("Mount Mining Laser 1"),

            @SerializedName("MOUNT_MINING_LASER_II")
            MountMiningLaser2("Mount Mining Laser 2"),

            @SerializedName("MOUNT_MINING_LASER_III")
            MountMiningLaser3("Mount Mining Laser 3"),

            @SerializedName("MOUNT_LASER_CANNON_I")
            MountLaserCannon1("Mount Laser Cannon 1"),

            @SerializedName("MOUNT_MISSILE_LAUNCHER_I")
            MountMissileLauncher1("Mount Missile Launcher 1"),

            @SerializedName("MOUNT_TURRET_I")
            MountTurret1("Mount Turret 1")
        }
    }
}



