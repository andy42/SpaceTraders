package com.jaehl.spaceTraders.data.controllers

sealed class ShipState {
    object Wait : ShipState()
    object Mining : ShipState()
    object Surveying : ShipState()
    object Traveling : ShipState()
    data class TransportSell(val destination : String) : ShipState()
    object NavigateBackToAsteroidField : ShipState()
    object Refine : ShipState()
    object CargoWait : ShipState()
}