package com.jaehl.spaceTraders.ui.pages.shipDetails


import androidx.compose.runtime.mutableStateOf
import com.jaehl.spaceTraders.data.model.CargoData
import com.jaehl.spaceTraders.data.model.Ship
import com.jaehl.spaceTraders.data.model.WaypointType
import com.jaehl.spaceTraders.data.model.response.Cooldown
import com.jaehl.spaceTraders.data.repo.CargoInfoRepo
import com.jaehl.spaceTraders.data.services.FleetService
import com.jaehl.spaceTraders.ui.util.ViewModel
import com.jaehl.spaceTraders.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.jaehl.spaceTraders.ui.navigation.NavBackListener
import com.jaehl.spaceTraders.ui.navigation.NavSystemListener
import kotlinx.coroutines.delay
import java.time.Duration
import java.util.Date
import java.util.Timer
import kotlin.concurrent.fixedRateTimer
import kotlin.math.abs

class ShipDetailsViewModel @Inject constructor(
    private val logger : Logger,
    private val fleetService : FleetService,
    private val cargoInfoRepo : CargoInfoRepo
) : ViewModel() {

    var navBackListener : NavBackListener? = null
    var navSystemListener : NavSystemListener? = null
    var navShipDetailsDialogListener : NavShipDetailsDialogListener? = null

    var coolDownTick = mutableStateOf(0)
    var isCoolingDown = mutableStateOf(false)

    private lateinit var shipId : String

    private var ship : Ship? = null

//    private var coolDownTime : Duration = Duration.ZERO
//    private var timer : Timer? = null

    var shipViewModel = mutableStateOf(ShipViewModel.create(Ship(), cargoInfoRepo))
        private set

    var miningViewModel = mutableStateOf(MiningViewModel())
        private set

    fun init(viewModelScope: CoroutineScope, shipId : String) {
        super.init(viewModelScope)
        this.shipId = shipId
        requestDataUpdate()
    }

//    private fun startCoolDownTimer(seconds : Long) {
//        coolDownTime = Duration.ofSeconds(seconds)
//        timer?.cancel()
//        timer = fixedRateTimer(
//            initialDelay = 0L,
//            period = 1000L
//        ){
//            coolDownTime = coolDownTime.minus(Duration.ofSeconds(1L))
//            if(coolDownTime < Duration.ZERO) {
//                coolDownTime = Duration.ZERO
//            }
//            updateUi()
//        }
//    }

    fun requestDataUpdate() = viewModelScope.launch {
        ship = fleetService.getShip(shipId)
        updateUi()
    }

    private fun updateUi(){
        val ship = this.ship ?: return

        miningViewModel.value = miningViewModel.value.copy(
            isVisible = ship.hasMiningLaser(),
            canMine = (ship.nav.status == Ship.Nav.Statue.Docked && ship.nav.route.destination.type == WaypointType.asteroidField)
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
        val ship = this@ShipDetailsViewModel.ship ?: return@launch

        try {
            val response = fleetService.shipExtract(shipId)
            this@ShipDetailsViewModel.ship = ship.copy(
                cargo = response.cargo
            )

            val delayTime = (response.cooldown.expiration.time - Date().time)
            miningViewModel.value = miningViewModel.value.copy(
                yield = MiningViewModel.Yield(
                    cargoInfoRepo.getCargoData(response.extraction.yield.symbol) ?: CargoData(),
                    units = response.extraction.yield.units
                ),
                isCoolingDown = true,
                coolDownTime = (delayTime/1000).toInt()
            )
            coolDownTick.value = (delayTime/1000).toInt()
            isCoolingDown.value = true
            updateUi()

            logger.log("delayTime : ${delayTime/1000}")
            delay(delayTime)
            isCoolingDown.value = false
            miningViewModel.value = miningViewModel.value.copy(
                isCoolingDown = false,
                coolDownTime = 0
            )
            logger.log("delay finished")

        }
        catch (t : Throwable) {
            logger.error(t.message ?: "startMiningClick Error")
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
            navSystemListener?.openSystemDetails(
                it.nav.systemSymbol,
                shipId = it.symbol,
                marketsOnly = (it.nav.flightMode == Ship.Nav.FlightMode.Docked),
                travelOptions = false)
        }
    }

    data class ShipViewModel(
        val shipId : String,
        val name : String,
        val state : StatusViewModel,
        val nav : NavViewModel,
        val fuel : FuelViewModel,
        val cargo : CargoViewModel
    ) {
        companion object {
            fun create(ship: Ship, cargoInfoRepo : CargoInfoRepo) : ShipViewModel {
                return ShipViewModel(
                    shipId = ship.symbol,
                    name = ship.symbol,
                    state = StatusViewModel.create(ship),
                    nav = NavViewModel.create(ship),
                    fuel = FuelViewModel.create(ship),
                    cargo = CargoViewModel.create(ship, cargoInfoRepo)
                )
            }
        }
    }

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

    data class NavViewModel(
        val location : String,
        val locationType : String,
        val enabled : Boolean,
    ) {
        companion object {
            fun create(ship: Ship) : NavViewModel {
                return NavViewModel(
                    location = ship.nav.route.destination.symbol,
                    locationType = ship.nav.route.destination.type.value,
                    enabled = (ship.nav.status == Ship.Nav.Statue.InOrbit)
                )
            }
        }
    }


    data class FuelViewModel(
        val capacity : Int,
        val current : Int,
        val enabled : Boolean,
    ) {
        companion object {
            fun create(ship: Ship) : FuelViewModel {
                return FuelViewModel(
                    capacity = ship.fuel.capacity,
                    current = ship.fuel.current,
                    enabled = (ship.nav.status == Ship.Nav.Statue.Docked)
                )
            }
        }
    }

    data class MiningViewModel(
        val isVisible : Boolean = false,
        val canMine : Boolean = false,
        val isCoolingDown : Boolean = false,
        val coolDownTime : Int = 0,
        val yield : Yield? = null
    ) {
        data class Yield(
            val cargoData: CargoData,
            val units : Int
        )
    }

    data class CargoViewModel(
        val capacity : Int,
        val units : Int,
        val inventory : List<CargoItemViewModel>
    ) {

        data class CargoItemViewModel(
            val symbol: String,
            val name: String,
            val description: String,
            val units: Int,
            val isRefinable: Boolean,
            val canJettison: Boolean
        ) {
            companion object {
                fun create(ship: Ship, inventoryItem: Ship.Cargo.InventoryItem, cargoData: CargoData, hasRefinery : Boolean): CargoItemViewModel {
                    return CargoItemViewModel(
                        symbol = cargoData.symbol,
                        name = cargoData.name,
                        description = cargoData.description,
                        units = inventoryItem.units,
                        isRefinable = (cargoData.refineTo != null) && hasRefinery,
                        canJettison = (ship.nav.status == Ship.Nav.Statue.InOrbit)
                    )
                }
            }
        }

        companion object {
            fun create(ship: Ship, cargoInfoRepo : CargoInfoRepo) : CargoViewModel{
                val hasRefinery = ship.hasRefinery()
                return CargoViewModel(
                    capacity = ship.cargo.capacity,
                    units = ship.cargo.units,
                    inventory = ship.cargo.inventory.map {
                        CargoViewModel.CargoItemViewModel.create(
                            ship = ship,
                            inventoryItem = it,
                            cargoData = cargoInfoRepo.getCargoData(it),
                            hasRefinery = hasRefinery
                        )
                    }
                )
            }
        }
    }
}