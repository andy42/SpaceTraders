package com.jaehl.spaceTraders.ui.pages.homePage

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.jaehl.spaceTraders.data.repo.AgentRepo
import com.jaehl.spaceTraders.data.services.AgentService
import com.jaehl.spaceTraders.data.services.AuthService
import com.jaehl.spaceTraders.data.services.FleetService
import com.jaehl.spaceTraders.extensions.postSwap
import com.jaehl.spaceTraders.ui.component.ship.ShipViewModel
import com.jaehl.spaceTraders.ui.navigation.NavBackListener
import com.jaehl.spaceTraders.ui.navigation.NavShipListener
import com.jaehl.spaceTraders.ui.navigation.NavSystemListener
import com.jaehl.spaceTraders.ui.navigation.NavTaskListener
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
    var navTaskListener: NavTaskListener? = null

    var userName = mutableStateOf("")
        private set

    var ships = mutableStateListOf<ShipViewModel>()
        private set

    override fun init(viewModelScope: CoroutineScope) {
        super.init(viewModelScope)
        requestDataUpdate()
    }

    fun requestDataUpdate() = viewModelScope.launch {

        val agent = agentRepo.getFirstAgent()

        if (agent == null){
            navHomePageDialogListener?.openRegistrationDialog()
            return@launch
        }

        authService.setToken(agentRepo.getAgentToken(agent.symbol))

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

    fun onRunTaskClick() {
        navTaskListener?.openRunTask()
    }
}

