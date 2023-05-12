package com.jaehl.spaceTraders.ui.dialogs.jettisonCargo

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.jaehl.spaceTraders.data.services.FleetService
import com.jaehl.spaceTraders.extensions.postSwap
import com.jaehl.spaceTraders.ui.dialogs.buySellCargo.SliderValue
import com.jaehl.spaceTraders.ui.util.ViewModel
import com.jaehl.spaceTraders.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.ceil

class JettisonCargoDialogViewModel @Inject constructor(
    private val logger : Logger,
    private val fleetService: FleetService
) : ViewModel() {

    private lateinit var config : JettisonCargoDialogConfig

    var title = mutableStateOf("")
        private set

    var cargoName = mutableStateOf("")
    var valueSlider = mutableStateOf(SliderValue())
    var jettisonAmountText = mutableStateOf("")

    fun init(viewModelScope: CoroutineScope, config : JettisonCargoDialogConfig) {
        super.init(viewModelScope)

        this.config = config


        viewModelScope.launch {
            val cargo = fleetService.getShip(config.shipId).cargo.inventory.firstOrNull {it.symbol == config.cargoId} ?: return@launch
            cargoName.value = cargo.name
            valueSlider.value = SliderValue(
                show = true,
                minValue = 1f,
                maxValue = cargo.units.toFloat(),
                currentValue = cargo.units.toFloat()
            )
            updateJettisonAmountText()

        }
    }
    private fun updateJettisonAmountText() {
        jettisonAmountText.value = "Jettison : ${ceil(valueSlider.value.currentValue).toInt()}"
    }

    fun onCloseClick() = viewModelScope.launch {
        config.onDismissed()
    }

    fun onValueChange(value : Float) {
        valueSlider.value = valueSlider.value.copy(
            currentValue = value
        )
        updateJettisonAmountText()
    }

    fun onJettisonClick() = viewModelScope.launch {
        val units = ceil(valueSlider.value.currentValue).toInt()
        fleetService.shipJettisonCargo(shipId = config.shipId, cargoId = config.cargoId, units = units)
        config.onDismissed()
    }
}

data class SliderValue(
    val show : Boolean = false,
    val minValue : Float = 0f,
    val maxValue : Float = 0f,
    val currentValue : Float = 0f
)