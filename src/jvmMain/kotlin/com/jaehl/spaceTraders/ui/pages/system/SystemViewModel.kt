package com.jaehl.spaceTraders.ui.pages.system
import kotlin.math.*

import androidx.compose.runtime.mutableStateListOf
import com.jaehl.spaceTraders.data.model.Ship
import com.jaehl.spaceTraders.data.model.WaypointType
import com.jaehl.spaceTraders.data.services.FleetService
import com.jaehl.spaceTraders.data.services.SystemService
import com.jaehl.spaceTraders.extensions.postSwap
import com.jaehl.spaceTraders.ui.util.ViewModel
import com.jaehl.spaceTraders.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.jaehl.spaceTraders.ui.navigation.NavBackListener
import com.jaehl.spaceTraders.ui.navigation.NavSystemListener

class SystemViewModel @Inject constructor(
    private val logger : Logger,
    private val systemService : SystemService,
    private val fleetService : FleetService,
) : ViewModel() {

    var navBackListener : NavBackListener? = null
    var navSystemListener : NavSystemListener? = null

    var systemWaypoints = mutableStateListOf<SystemWaypointViewModel>()
        private set

    private lateinit var systemId : String
    private var shipId : String? = null
    private var ship : Ship? = null
    private var marketsOnly : Boolean = false
    private var travelOptions : Boolean = false

    fun init(viewModelScope: CoroutineScope,
             systemId : String,
             shipId :
             String?,
             marketsOnly : Boolean,
             travelOptions : Boolean

    ) {
        super.init(viewModelScope)

        this.systemId = systemId
        this.shipId = shipId
        this.marketsOnly = marketsOnly
        this.travelOptions = travelOptions

        viewModelScope.launch {
            val shipId = this@SystemViewModel.shipId
            val ship = if(shipId != null) fleetService.getShip(shipId) else null

            val response = systemService.getSystemWaypoints(systemId)
            systemWaypoints.postSwap(

                response.data.map {
                    val distance = ship?.getPosition()?.distance(it.getPosition())
                    SystemWaypointViewModel(
                        symbol = it.symbol,
                        type = it.type,
                        distance = if(distance != null) ceil(abs(distance)).toInt() else null,
                        hasMarketplace = it.hasMarketplace(),
                        hasShipyard = it.hasShipyard(),
                        travelTo = (ship != null && ship.nav.route.destination.symbol != it.symbol && travelOptions)
                    )
                }
            )
        }
    }

    fun onBackClick() {
        navBackListener?.navigateBack()
    }

    fun openMarket(waypointId : String) {
        navSystemListener?.openMarket(
            systemId = systemId,
            waypointId = waypointId,
            shipId = shipId
        )
    }

    fun openShipyard(waypointId : String) {

    }

    fun travelTo(waypointId : String) = viewModelScope.launch {
        shipId?.let {
            fleetService.shipNavigate(
                shipId = it,
                waypointId = waypointId
            )
        }
    }

    data class SystemWaypointViewModel(
        val symbol : String,
        val type : WaypointType,
        val distance : Int? = null,
        val hasMarketplace : Boolean,
        val hasShipyard : Boolean,
        val travelTo : Boolean
    )
}