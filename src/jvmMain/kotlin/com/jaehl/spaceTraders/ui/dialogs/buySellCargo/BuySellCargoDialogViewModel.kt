package com.jaehl.spaceTraders.ui.dialogs.buySellCargo

import androidx.compose.runtime.mutableStateOf
import com.jaehl.spaceTraders.data.model.Ship
import com.jaehl.spaceTraders.data.model.TradeGood
import com.jaehl.spaceTraders.data.repo.MarketRepo
import com.jaehl.spaceTraders.data.services.FleetService
import com.jaehl.spaceTraders.ui.util.ViewModel
import com.jaehl.spaceTraders.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.ceil

class BuySellCargoDialogViewModel @Inject constructor(
    private val logger : Logger,
    private val marketRepo: MarketRepo,
    private val fleetService: FleetService
) : ViewModel() {

    private lateinit var config : BuySellCargoDialogConfig

    var cargoName = mutableStateOf("")

    var showTransactionTypePicker = mutableStateOf(true)
    var transactionType = mutableStateOf(TransactionType.Sell)
    var valueSlider = mutableStateOf(SliderValue())
    var errorMessage = mutableStateOf(ErrorMessage())
    var transactionViewModel = mutableStateOf(TransactionViewModel())

    var title = mutableStateOf("buy Sell Cargo")
        private set

    private var ship : Ship? = null
    private var tradeGood : TradeGood? = null

    private fun getCargoAmount(ship: Ship, cargoId : String) : Int {
        val cargo = ship.cargo.inventory.firstOrNull{ it.symbol == cargoId} ?: return 0
        return cargo.units
    }

    fun init(viewModelScope: CoroutineScope, config : BuySellCargoDialogConfig) {
        super.init(viewModelScope)

        this.config = config

        viewModelScope.launch {
            cargoName.value = config.cargoId
            tradeGood = marketRepo.getMarketHistory(systemId = config.systemId, waypointId = config.waypointId)?.getLatestTradeGood(config.cargoId)
            ship = fleetService.getShip(config.shipId)

            if(initUi()) {
                setupSlider()
                updateTransaction()
            }
        }
    }

    private fun initUi() : Boolean{
        val ship = this.ship ?: return false

        val maxBuyCapacity = ship.cargo.capacity - ship.cargo.units
        val maxSellCapacity = getCargoAmount(
            ship =ship,
            cargoId = config.cargoId
        )

        if(maxBuyCapacity == 0 && maxSellCapacity == 0){
            showTransactionTypePicker.value = false
            errorMessage.value = ErrorMessage(
                isError = true,
                message = "No cargo room to Buy or items to sell"
            )
            return false
        }
        else if (transactionType.value == TransactionType.Buy && maxBuyCapacity == 0) {
            transactionType.value = TransactionType.Sell
            showTransactionTypePicker.value = false
        }
        else if (transactionType.value == TransactionType.Sell && maxSellCapacity == 0) {
            transactionType.value = TransactionType.Buy
            showTransactionTypePicker.value = false
        }
        else {
            showTransactionTypePicker.value = true
        }
        return true
    }

    private fun setupSlider(){
        val ship = this.ship ?: return

        val maxBuyCapacity = ship.cargo.capacity - ship.cargo.units
        val maxSellCapacity = getCargoAmount(
            ship =ship,
            cargoId = config.cargoId
        )
        if(transactionType.value == TransactionType.Buy) {
            valueSlider.value = SliderValue(
                show = true,
                minValue = 1f,
                maxValue = maxBuyCapacity.toFloat(),
                currentValue = 1f
            )
        }
        else if (transactionType.value == TransactionType.Sell) {
            valueSlider.value = SliderValue(
                show = true,
                minValue = 1f,
                maxValue = maxSellCapacity.toFloat(),
                currentValue = maxSellCapacity.toFloat()
            )
        }
    }

    private fun updateTransaction(){
        val quantity = ceil(valueSlider.value.currentValue).toInt()
        if(transactionType.value == TransactionType.Buy){
            transactionViewModel.value = TransactionViewModel(
                show = true,
                transactionType = TransactionType.Buy,
                price = tradeGood?.purchasePrice ?: 0,
                quantity = quantity,
                total = quantity * (tradeGood?.purchasePrice ?: 0)
            )
        }
        else if (transactionType.value == TransactionType.Sell){
            transactionViewModel.value = TransactionViewModel(
                show = true,
                transactionType = TransactionType.Sell,
                price = tradeGood?.sellPrice ?: 0,
                quantity = quantity,
                total = quantity * (tradeGood?.sellPrice ?: 0)
            )
        }
    }

    fun onCloseClick() = viewModelScope.launch {
        config.onDismissed()
    }

    fun onTransactionTypeChange(type : TransactionType) {
        transactionType.value = type
        setupSlider()
    }

    fun onValueChange(value : Float) {
        valueSlider.value = valueSlider.value.copy(
            currentValue = value
        )
        updateTransaction()
    }

    fun onActionClick() = viewModelScope.launch {
        val quantity = ceil(valueSlider.value.currentValue).toInt()
        if(transactionType.value == TransactionType.Buy) {
            fleetService.shipPurchaseCargo(shipId = config.shipId, cargoId = config.cargoId, units = quantity)
        } else {
            fleetService.shipSellCargo(shipId = config.shipId, cargoId = config.cargoId, units = quantity)
        }
        config.onDismissed()
    }
}

data class SliderValue(
    val show : Boolean = false,
    val minValue : Float = 0f,
    val maxValue : Float = 0f,
    val currentValue : Float = 0f
)

enum class TransactionType(val value : Int) {
    Buy(1),
    Sell(0);

    companion object {
        fun fromValue(value : Int) : TransactionType {
            return when(value) {
                0 -> Buy
                else -> Sell
            }
        }
    }
}

data class TransactionViewModel (
    val show : Boolean = false,
    val transactionType : TransactionType = TransactionType.Buy,
    val price : Int = 0,
    val quantity : Int = 0,
    val total : Int = 0
)

data class ErrorMessage(
    val isError : Boolean = false,
    val message : String = ""
)