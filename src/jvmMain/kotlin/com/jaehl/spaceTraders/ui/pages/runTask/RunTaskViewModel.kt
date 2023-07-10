package com.jaehl.spaceTraders.ui.pages.runTask

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.jaehl.spaceTraders.data.repo.SystemRepo
import com.jaehl.spaceTraders.data.services.FleetService
import com.jaehl.spaceTraders.extensions.postSwap
import com.jaehl.spaceTraders.ui.util.ViewModel
import com.jaehl.spaceTraders.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.jaehl.spaceTraders.ui.navigation.NavBackListener
import com.jaehl.spaceTraders.ui.pages.runTask.agents.AgentDetails
import com.jaehl.spaceTraders.ui.pages.runTask.tasks.AgentRole
import com.jaehl.spaceTraders.ui.pages.runTask.tasks.AgentRunnerTask
import com.jaehl.spaceTraders.ui.pages.runTask.tasks.TaskUpdateListener
import com.jaehl.spaceTraders.ui.pages.shipDetails.tasks.OreMineRefineSellTask
import com.jaehl.spaceTraders.util.DateHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class RunTaskViewModel @Inject constructor(
    private val logger : Logger,
    private val oreMineRefineSellTask: OreMineRefineSellTask,
    private val agentRunnerTask: AgentRunnerTask,
    private val fleetService : FleetService,
    private val systemRepo: SystemRepo,
    private val dateHelper: DateHelper
) : ViewModel(), TaskUpdateListener{

    var navBackListener : NavBackListener? = null

    var taskRunning = mutableStateOf(false)

    var agentRoles = mutableStateListOf<AgentRole>()
        private set

    var agentStates = mutableStateListOf<AgentDetailsViewModel>()
        private set


    var agentRoleDropDownAgentId = mutableStateOf("")
    var agentRoleTypes = mutableStateListOf<AgentRole.Role>()
        private set

    override fun init(viewModelScope: CoroutineScope) {
        super.init(viewModelScope)

        viewModelScope.launch {
            agentRoleTypes.postSwap(
                listOf(
                    AgentRole.Role.None,
                    AgentRole.Role.MarketScout,
                    AgentRole.Role.StaticMiner
                )
            )
        }

        requestDataUpdate()
    }

    fun openAgentRoleDropDown(agentShipId : String) {
        agentRoleDropDownAgentId.value = agentShipId
    }

    fun onAgentRoleSelected(agentShipId : String, role : AgentRole.Role) = viewModelScope.launch {
        agentRoleDropDownAgentId.value = ""

        val list = agentRoles.toMutableList()
        val index = agentRoles.indexOfFirst { it.shipId ==  agentShipId}
        if(index == -1 ) return@launch
        list[index] = list[index].copy(
            role = role
        )
        agentRoles.postSwap(list)
    }

    fun requestDataUpdate() = viewModelScope.launch {
        agentRoles.postSwap(
            fleetService.getShips(1).data.map { ship ->
                AgentRole(
                    shipId = ship.symbol,
                    role = AgentRole.Role.StaticMiner
                )
            }
        )

        updateUi()
    }

    fun updateUi(){

    }

    fun onBackClick() {
        navBackListener?.navigateBack()
    }

    override fun onTaskUpdate(agentDetailsList: List<AgentDetails>) {
        viewModelScope.launch {
            this@RunTaskViewModel.agentStates.postSwap(
                agentDetailsList.map { AgentDetailsViewModel.create(it) }
            )
        }
    }

    fun updateAgentTimers() = viewModelScope.launch {
        val agentStates = this@RunTaskViewModel.agentStates.toMutableList()
        agentStates.forEachIndexed { index, agentDetailsViewModel ->
            val coolDown = ((agentDetailsViewModel.coolDownTime?.time ?: 0) - dateHelper.getNow().time) /1000
            agentStates[index] = agentDetailsViewModel.copy(
                coolDownString = " CoolDown : ${coolDown.toInt()}"
            )
        }
        this@RunTaskViewModel.agentStates.postSwap(agentStates)
    }

    fun startTaskClick() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            taskRunning.value = true
            agentRunnerTask.setups(
                agentRoleList = agentRoles.filter { it.role != AgentRole.Role.None },
                taskUpdateListener = this@RunTaskViewModel
            )
            agentRunnerTask.run()
            taskRunning.value = false
        }
    }
}

data class AgentDetailsViewModel(
    val shipId : String,
    val name : String,
    val state : String,
    val fuel : String,
    val cargo : String,
    val coolDownTime : Date? = null,
    val coolDownString : String = "",
    val stateDescription : String
) {
    companion object {
        fun create(agentDetails: AgentDetails) : AgentDetailsViewModel {
            return AgentDetailsViewModel(
                shipId = agentDetails.ship.symbol,
                name = agentDetails.ship.symbol,
                state = agentDetails.ship.nav.status.name,
                fuel = "Fuel (${agentDetails.ship.fuel.current} : ${agentDetails.ship.fuel.capacity})",
                cargo = "Cargo (${agentDetails.ship.cargo.units} : ${agentDetails.ship.cargo.capacity})",
                stateDescription = agentDetails.stateDescription,
                coolDownTime = agentDetails.coolDown.expiration
            )
        }
    }
}