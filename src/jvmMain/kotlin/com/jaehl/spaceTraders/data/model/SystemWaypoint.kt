package com.jaehl.spaceTraders.data.model

import com.google.gson.annotations.SerializedName

data class SystemWaypoint(
    val symbol : String = "",
    val type : WaypointType = WaypointType.planet,
    val systemSymbol : String = "",
    val x : Int = 0,
    val y : Int = 0,
    val orbitals : List<Symbol> = listOf(),
    val faction : Symbol = Symbol(),
    val traits : List<WaypointTrait> = listOf()
) {
    fun hasMarketplace() : Boolean {
        return (traits.firstOrNull { it.symbol == TraitType.Marketplace } != null)
    }
    fun hasShipyard() : Boolean {
        return (traits.firstOrNull { it.symbol == TraitType.Shipyard } != null)
    }
    fun getPosition() : Vector2d {
        return Vector2d(x, y)
    }
}

enum class WaypointType(val value : String){
    @SerializedName("PLANET")
    planet("Planet"),

    @SerializedName("GAS_GIANT")
    gasGiant("Gas Giant"),

    @SerializedName("MOON")
    moon("Moon"),

    @SerializedName("ORBITAL_STATION")
    orbitalStation("Orbital Station"),

    @SerializedName("JUMP_GATE")
    jumpGate("Jump Gate"),

    @SerializedName("ASTEROID_FIELD")
    asteroidField("Asteroid Field"),

    @SerializedName("NEBULA")
    nebula("Nebula"),

    @SerializedName("DEBRIS_FIELD")
    debrisField("Debris Field"),

    @SerializedName("GRAVITY_WELL")
    gravityWell("Gravit yWell")
}

data class WaypointTrait(
    val symbol : TraitType = TraitType.Uncharted,
    val name : String = "",
    val description : String = ""
)

enum class TraitType(val value : String) {
    @SerializedName("UNCHARTED")
    Uncharted("Uncharted"),

    @SerializedName("MARKETPLACE")
    Marketplace("Marketplace"),

    @SerializedName("SHIPYARD")
    Shipyard("Shipyard"),

    @SerializedName("OUTPOST")
    Outpost("Outpost"),

    @SerializedName("SCATTERED_SETTLEMENTS")
    ScatteredSettlements("ScatteredSettlements"),

    @SerializedName("SPRAWLING_CITIES")
    SprawlingCities("SprawlingCities"),

    @SerializedName("MEGA_STRUCTURES")
    MegaStructures("MegaStructures"),

    @SerializedName("OVERCROWDED")
    Overcrowded("Overcrowded"),

    @SerializedName("HIGH_TECH")
    HighTech("HighTech"),

    @SerializedName("CORRUPT")
    Corrupt("Corrupt"),

    @SerializedName("BUREAUCRATIC")
    Bureaucratic("Bureaucratic"),

    @SerializedName("TRADING_HUB")
    TradingHub("TradingHub"),

    @SerializedName("INDUSTRIAL")
    Industrial("Industrial"),

    @SerializedName("BLACK_MARKET")
    BlackMarket("BlackMarket"),

    @SerializedName("RESEARCH_FACILITY")
    ResearchFacility("ResearchFacility"),

    @SerializedName("MILITARY_BASE")
    MilitaryBase("MilitaryBase"),

    @SerializedName("SURVEILLANCE_OUTPOST")
    SurveillanceOutpost("SurveillanceOutpost"),

    @SerializedName("EXPLORATION_OUTPOST")
    ExplorationOutpost("ExplorationOutpost"),

    @SerializedName("MINERAL_DEPOSITS")
    MineralDeposits("MineralDeposits"),

    @SerializedName("COMMON_METAL_DEPOSITS")
    CommonMetalDeposits("CommonMetalDeposits"),

    @SerializedName("PRECIOUS_METAL_DEPOSITS")
    PreciousMetalDeposits("PreciousMetalDeposits"),

    @SerializedName("RARE_METAL_DEPOSITS")
    RareMetalDeposits("RareMetalDeposits"),

    @SerializedName("METHANE_POOLS")
    MethanePools("MethanePools"),

    @SerializedName("ICE_CRYSTALS")
    IceCrystals("IceCrystals"),

    @SerializedName("EXPLOSIVE_GASES")
    ExplosiveGases("ExplosiveGases"),

    @SerializedName("STRONG_MAGNETOSPHERE")
    StrongMagnetosphere("StrongMagnetosphere"),

    @SerializedName("VIBRANT_AURORAS")
    VibrantAuroras("VibrantAuroras"),

    @SerializedName("SALT_FLATS")
    SaltFlats("SaltFlats"),

    @SerializedName("CANYONS")
    Canyons("Canyons"),

    @SerializedName("PERPETUAL_DAYLIGHT")
    PerpetualDaylight("PerpetualDaylight"),

    @SerializedName("PERPETUAL_OVERCAST")
    PerpetualOvercast("PerpetualOvercast"),

    @SerializedName("DRY_SEABEDS")
    DrySeabeds("DrySeabeds"),

    @SerializedName("MAGMA_SEAS")
    MagmaSeas("MagmaSeas"),

    @SerializedName("SUPERVOLCANOES")
    Supervolcanoes("Supervolcanoes"),

    @SerializedName("ASH_CLOUDS")
    AshClouds("AshClouds"),

    @SerializedName("VAST_RUINS")
    VastRuins("VastRuins"),

    @SerializedName("MUTATED_FLORA")
    MutatedFlora("MutatedFlora"),

    @SerializedName("TERRAFORMED")
    Terraformed("Terraformed"),

    @SerializedName("EXTREME_PRESSURE")
    ExtremePressure("ExtremePressure"),

    @SerializedName("DIVERSE_LIFE")
    DiverseLife("DiverseLife"),

    @SerializedName("SCARCE_LIFE")
    ScarceLife("ScarceLife"),

    @SerializedName("FOSSILS")
    Fossils("Fossils"),

    @SerializedName("WEAK_GRAVITY")
    WeakGravity("WeakGravity"),

    @SerializedName("STRONG_GRAVITY")
    StrongGravity("StrongGravity"),

    @SerializedName("CRUSHING_GRAVITY")
    CrushingGravity("CrushingGravity"),

    @SerializedName("TOXIC_ATMOSPHERE")
    ToxicAtmosphere("ToxicAtmosphere"),

    @SerializedName("CORROSIVE_ATMOSPHERE")
    CorrosiveAtmosphere("CorrosiveAtmosphere"),

    @SerializedName("BREATHABLE_ATMOSPHERE")
    BreathableAtmosphere("BreathableAtmosphere"),

    @SerializedName("JOVIAN")
    Jovian("Jovian"),

    @SerializedName("ROCKY")
    Rocky("Rocky"),

    @SerializedName("VOLCANIC")
    Volcanic("Volcanic"),

    @SerializedName("FROZEN")
    Frozen("Frozen"),

    @SerializedName("SWAMP")
    Swamp("Swamp"),

    @SerializedName("BARREN")
    Barren("Barren"),

    @SerializedName("TEMPERATE")
    Temperate("Temperate"),

    @SerializedName("JUNGLE")
    Jungle("Jungle"),

    @SerializedName("OCEAN")
    Ocean("Ocean"),

    @SerializedName("STRIPPED")
    Stripped("Stripped")
}