package com.jaehl.spaceTraders.ui.pages.homePage

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.jaehl.spaceTraders.data.model.Ship
import com.jaehl.spaceTraders.data.repo.AgentRepo
import com.jaehl.spaceTraders.data.services.AgentService
import com.jaehl.spaceTraders.data.services.AuthService
import com.jaehl.spaceTraders.data.services.FleetService
import com.jaehl.spaceTraders.extensions.postSwap
import com.jaehl.spaceTraders.ui.navigation.NavBackListener
import com.jaehl.spaceTraders.ui.navigation.NavShipListener
import com.jaehl.spaceTraders.ui.navigation.NavSystemListener
import com.jaehl.spaceTraders.ui.util.TestHelper
import com.jaehl.spaceTraders.ui.util.ViewModel
import com.jaehl.spaceTraders.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val logger: Logger,
    private val authService : AuthService,
    private val agentService : AgentService,
    private val fleetService : FleetService,
    private val agentRepo : AgentRepo
) : ViewModel() {

    var navBackListener : NavBackListener? = null
    var navShipListener : NavShipListener? = null
    var navSystemListener : NavSystemListener? = null
    var navHomePageDialogListener : NavHomePageDialogListener? = null

    var userName = mutableStateOf("")
        private set

    var ships = mutableStateListOf<ShipViewModel>()
        private set

    override fun init(viewModelScope: CoroutineScope) {
        super.init(viewModelScope)
        //logger.log(TestHelper.createEnumString())
        requestDataUpdate()
    }

    fun requestDataUpdate() = viewModelScope.launch {

        val agent = agentRepo.getFirstAgent()

        if (agent == null){
            navHomePageDialogListener?.openRegistrationDialog()
            return@launch
        }

        authService.setToken(agentRepo.getAgentToken(agent.symbol))

        //val agent = agentService.getAgent()
        userName.value = agent.symbol

        val shipsPaged = fleetService.getShips(page = 1)
        ships.postSwap(shipsPaged.data
            .map {
                ShipViewModel.create(it)
            })
    }

    fun onBackClick() {
        navBackListener?.navigateBack()
    }

    fun onShipClick(shipId : String) {
        navShipListener?.openShipDetails(shipId = shipId )
    }

    fun openSystemSearchClick() {
        navSystemListener?.openSystemSearch()
    }

    data class ShipViewModel(
        val shipId : String,
        val name : String,
        val state : String,
        val fuel : String,
        val cargo : String
    ) {
        companion object {
            fun create(ship: Ship) : ShipViewModel {
                return ShipViewModel(
                    shipId = ship.symbol,
                    name = ship.symbol,
                    state = ship.nav.status.name,
                    fuel = "Fuel (${ship.fuel.current} : ${ship.fuel.capacity})",
                    cargo = "Cargo (${ship.cargo.units} : ${ship.cargo.capacity})"
                )
            }
        }
    }
}

