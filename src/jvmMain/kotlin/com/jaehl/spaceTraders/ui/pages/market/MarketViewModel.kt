package com.jaehl.spaceTraders.ui.pages.market

import androidx.compose.runtime.mutableStateListOf
import com.google.gson.annotations.SerializedName
import com.jaehl.spaceTraders.data.model.*
import com.jaehl.spaceTraders.data.repo.MarketRepo
import com.jaehl.spaceTraders.data.services.FleetService
import com.jaehl.spaceTraders.data.services.SystemService
import com.jaehl.spaceTraders.extensions.postSwap
import com.jaehl.spaceTraders.ui.util.ViewModel
import com.jaehl.spaceTraders.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.jaehl.spaceTraders.ui.navigation.NavBackListener
import java.util.*
import kotlin.collections.HashMap

class MarketViewModel @Inject constructor(
    private val logger : Logger,
    private val systemService : SystemService,
    private val fleetService: FleetService,
    private val marketRepo: MarketRepo
) : ViewModel() {

    var navBackListener : NavBackListener? = null
    var navMarketDialogListener : NavMarketDialogListener? = null

    var imports = mutableStateListOf<String>()
    var exports = mutableStateListOf<String>()

    var hasTradeData = mutableListOf<Boolean>(false)
    var tradeGoods = mutableStateListOf<MarketItemViewModel>()

    private lateinit var systemId : String
    private lateinit var waypointId : String
    private var shipId : String? = null

    private fun transformMarketHistory(marketHistory: MarketHistory, ship: Ship?) : List<MarketItemViewModel> {
        return marketHistory.tradeHistory.firstOrNull()?.tradeGoods?.map {tradeGood ->

            val amount = ship?.cargo?.inventory?.firstOrNull{ it.symbol == tradeGood.symbol }?.units
            MarketItemViewModel(
                symbol = tradeGood.symbol,
                name = tradeGood.symbol,
                tradeVolume = tradeGood.tradeVolume,
                supply = tradeGood.supply,
                purchasePrice = tradeGood.purchasePrice,
                sellPrice = "${tradeGood.sellPrice}  ${if(amount != null) ": (Cargo : $amount)" else ""}"
            )
        } ?: listOf()
    }

    fun init(viewModelScope: CoroutineScope, systemId : String, waypointId : String, shipId : String?) {
        super.init(viewModelScope)

        this.systemId = systemId
        this.waypointId = waypointId
        this.shipId = shipId

        update()
    }

    fun update() = viewModelScope.launch {
        val reponse = marketRepo.updateMarket(
            systemId = systemId,
            waypointId = waypointId
        ) ?: return@launch

        val ship = shipId?.let {  fleetService.getShip(it) }


        imports.postSwap(
            reponse.imports.map { it.name }
        )
        exports.postSwap(
            reponse.exports.map { it.name }
        )

        tradeGoods.postSwap(
            transformMarketHistory(reponse, ship)
        )
    }

    fun onBackClick() {
        navBackListener?.navigateBack()
    }

    fun onBuySellClick(cargoId : String) {
        shipId?.let {
            navMarketDialogListener?.openBuySellCargoDialog(
                systemId = systemId,
                waypointId = waypointId,
                shipId = it,
                cargoId = cargoId
            )
        }
    }
}

data class MarketItemViewModel(
    val symbol : String = "",
    val name : String = "",
    val tradeVolume : Int = 0,
    val supply : TradeGood.Supply = TradeGood.Supply.Scarce,
    val purchasePrice : Int = 0,
    val sellPrice : String = "",
)
