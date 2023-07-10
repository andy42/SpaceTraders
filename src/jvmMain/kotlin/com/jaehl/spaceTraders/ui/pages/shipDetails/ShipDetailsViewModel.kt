package com.jaehl.spaceTraders.ui.pages.shipDetails


import androidx.compose.runtime.mutableStateOf
import com.jaehl.spaceTraders.data.model.CargoData
import com.jaehl.spaceTraders.data.model.Ship
import com.jaehl.spaceTraders.data.model.WaypointType
import com.jaehl.spaceTraders.data.repo.CargoInfoRepo
import com.jaehl.spaceTraders.data.services.FleetService
import com.jaehl.spaceTraders.ui.util.ViewModel
import com.jaehl.spaceTraders.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.jaehl.spaceTraders.ui.navigation.NavBackListener
import com.jaehl.spaceTraders.ui.navigation.NavSystemListener
import com.jaehl.spaceTraders.ui.pages.shipDetails.tasks.*
import com.jaehl.spaceTraders.ui.pages.shipDetails.viewModel.MiningSurveyViewModel
import com.jaehl.spaceTraders.ui.pages.shipDetails.viewModel.MiningViewModel
import com.jaehl.spaceTraders.ui.pages.shipDetails.viewModel.ShipViewModel
import com.jaehl.spaceTraders.ui.pages.shipDetails.viewModel.StatusViewModel
import kotlinx.coroutines.delay
import java.util.Date

class ShipDetailsViewModel @Inject constructor(
    private val logger : Logger,
    private val fleetService : FleetService,
    private val cargoInfoRepo : CargoInfoRepo,
    private val task : Task,
    private val marketSearchTask: MarketSearchTask,
    private val soloMiningTask : SoloMiningTask,
    private val basicTasks : BasicTasks,
    private val oreMiningSellingTask : OreMiningSellingTask,
    private val oreMineRefineSellTask : OreMineRefineSellTask,
    private val contractTask : ContractTask
) : ViewModel() {

    var navBackListener : NavBackListener? = null
    var navSystemListener : NavSystemListener? = null
    var navShipDetailsDialogListener : NavShipDetailsDialogListener? = null

    var coolDownTick = mutableStateOf(0)
    var isCoolingDown = mutableStateOf(false)
    var coolingDownExpiration = mutableStateOf(Date())
    var coolingDownSeconds = mutableStateOf(0)

    var isInTransit = mutableStateOf(false)
    var secondsTillArrival = mutableStateOf(0)

    var taskButtonEnabled = mutableStateOf(true)

    private lateinit var shipId : String

    private var ship : Ship? = null

    var shipViewModel = mutableStateOf(ShipViewModel.create(Ship(), cargoInfoRepo))
        private set

    var miningViewModel = mutableStateOf(MiningViewModel())
        private set

    var miningSurveyViewModel = mutableStateOf(MiningSurveyViewModel())
        private set

    fun init(viewModelScope: CoroutineScope, shipId : String) {
        super.init(viewModelScope)
        this.shipId = shipId
        requestDataUpdate()
    }

    private suspend fun updateNavStatus(){
        ship?.let {
            if(it.nav.status == Ship.Nav.Statue.InTransit) {
                isInTransit.value = true
                viewModelScope.launch {
                    val delay = it.nav.route.arrival.time - Date().time
                    delay(delay)
                    isInTransit.value = false

                    ship = it.copy(
                        nav = it.nav.copy(
                            status = Ship.Nav.Statue.InOrbit
                        )
                    )
                    updateUi()
                }
            }
        }
    }
    fun requestDataUpdate() = viewModelScope.launch {
        ship = fleetService.getShip(shipId)
        updateNavStatus()
        updateUi()
    }

    private fun updateUi(){
        val ship = this.ship ?: return

        miningViewModel.value = miningViewModel.value.copy(
            isVisible = ship.hasMiningLaser(),
            canMine = (ship.nav.status == Ship.Nav.Statue.Docked && ship.nav.route.destination.type == WaypointType.asteroidField)
        )

        miningSurveyViewModel.value = miningSurveyViewModel.value.copy(
            isVisible = ship.hasSurveyor(),
            canScan = (ship.nav.status == Ship.Nav.Statue.InOrbit && ship.nav.route.destination.type == WaypointType.asteroidField),
        )

        shipViewModel.value = ShipViewModel.create(ship, cargoInfoRepo)
    }

    fun onBackClick() {
        navBackListener?.navigateBack()
    }

    fun onStateActionClick(action : StatusViewModel.Action) = viewModelScope.launch {
        val ship = this@ShipDetailsViewModel.ship ?: return@launch
        when (action) {
            StatusViewModel.Action.Dock -> {
                val nav = fleetService.shipDock(shipId)
                this@ShipDetailsViewModel.ship = ship.copy(
                    nav = nav
                )
            }
            StatusViewModel.Action.EnterOrbit -> {
                val nav = fleetService.shipEnterOrbit(shipId)
                this@ShipDetailsViewModel.ship = ship.copy(
                    nav = nav
                )
            }
        }
        updateUi()
    }

    fun onRefuelClick() = viewModelScope.launch {
        val ship = this@ShipDetailsViewModel.ship ?: return@launch
        val fuelResponse = fleetService.shipRefuel(shipId)
        this@ShipDetailsViewModel.ship = ship.copy(
            fuel = fuelResponse.fuel
        )
        updateUi()
    }

    fun refineCargoItemClick(symbol : String) = viewModelScope.launch {
        val ship = this@ShipDetailsViewModel.ship ?: return@launch
        val produce = cargoInfoRepo.getCargoData(symbol)?.refineTo ?: return@launch
        val response = fleetService.shipRefineMaterials(shipId, produce)
        this@ShipDetailsViewModel.ship = ship.copy(
            cargo = response.cargo
        )
        updateUi()
    }

    fun onJettisonCargoClick(cargoId : String){
        navShipDetailsDialogListener?.openJettisonCargoDialog(shipId = shipId, cargoId = cargoId)
    }

    fun startMiningClick() = viewModelScope.launch {
        var ship = this@ShipDetailsViewModel.ship ?: return@launch

        try {
            ship = basicTasks.mine(ship){
                miningViewModel.value = miningViewModel.value.copy(
                    yield = MiningViewModel.Yield(
                        cargoInfoRepo.getCargoData(it.extraction.yield.symbol) ?: CargoData(),
                        units = it.extraction.yield.units
                    )
                )

                isCoolingDown.value = true
                coolingDownExpiration.value = it.cooldown.expiration
            }
            this@ShipDetailsViewModel.ship = ship
            isCoolingDown.value = false

            updateUi()

        }
        catch (t : Throwable) {
            logger.error(t.message ?: "startMiningClick Error")
        }
    }

    fun startMiningSurveyClick() = viewModelScope.launch {
        val ship = this@ShipDetailsViewModel.ship ?: return@launch

        try {
            val response = fleetService.shipMiningSurvey(shipId)

            miningSurveyViewModel.value = miningSurveyViewModel.value.copy(
                surveyResults = response.surveys
            )

            val delayTime = (response.cooldown.expiration.time - Date().time)

            coolDownTick.value = (delayTime/1000).toInt()
            isCoolingDown.value = true

            delay(delayTime)

            isCoolingDown.value = false
        }
        catch (t : Throwable) {
            logger.error(t.message ?: "startMiningSurveyClick Error")
        }
    }

    fun navigateToClick() {
        ship?.let {
            navSystemListener?.openSystemDetails(
                it.nav.systemSymbol,
                shipId = it.symbol,
                marketsOnly = (it.nav.flightMode == Ship.Nav.FlightMode.Docked),
                travelOptions = (it.nav.flightMode != Ship.Nav.FlightMode.Docked))
        }
    }

    fun viewSystemClick() {
        ship?.let {
            navSystemListener?.openSystemDetails(
                it.nav.systemSymbol,
                shipId = it.symbol,
                marketsOnly = false,
                travelOptions = false
            )
        }
    }

    fun onOpenMarketsClick(){
        ship?.let {
            navSystemListener?.openMarket(
                systemId =it.nav.systemSymbol,
                waypointId = ship?.nav?.waypointSymbol ?: "",
                shipId = it.symbol)
        }
    }
}