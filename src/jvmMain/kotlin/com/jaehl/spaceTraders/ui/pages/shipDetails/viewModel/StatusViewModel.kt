package com.jaehl.spaceTraders.ui.pages.shipDetails.viewModel

import com.jaehl.spaceTraders.data.model.Ship

data class StatusViewModel(
    val state : String,
    val actionState : Action,
    val enabled : Boolean,
) {
    companion object {
        fun create(ship: Ship) : StatusViewModel {
            return when(val state = ship.nav.status){
                Ship.Nav.Statue.Docked -> {
                    StatusViewModel(
                        state = state.value,
                        actionState = Action.EnterOrbit,
                        enabled = true
                    )
                }
                Ship.Nav.Statue.InOrbit -> {
                    StatusViewModel(
                        state = state.value,
                        actionState = Action.Dock,
                        enabled = true
                    )
                }
                Ship.Nav.Statue.InTransit -> {
                    StatusViewModel(
                        state = state.value,
                        actionState = Action.Dock,
                        enabled = false
                    )
                }
            }
        }
    }
    enum class Action(val value : String){
        Dock("Dock"),
        EnterOrbit("Enter Orbit")
    }
}