package com.jaehl.spaceTraders.ui.pages.market

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.arkivanov.decompose.router.overlay.OverlayNavigation
import com.arkivanov.decompose.router.overlay.activate
import com.arkivanov.decompose.router.overlay.childOverlay
import com.arkivanov.decompose.router.overlay.dismiss
import com.arkivanov.essenty.parcelable.Parcelable
import com.jaehl.spaceTraders.di.AppComponent
import com.jaehl.spaceTraders.ui.dialogs.buySellCargo.BuySellCargoDialogComponent
import com.jaehl.spaceTraders.ui.dialogs.buySellCargo.BuySellCargoDialogConfig
import com.jaehl.spaceTraders.ui.navigation.Component
import com.jaehl.spaceTraders.ui.navigation.NavBackListener
import javax.inject.Inject

interface NavMarketDialogListener {
    fun openBuySellCargoDialog(systemId : String, waypointId : String, shipId : String, cargoId : String)
}

class MarketComponent(
    appComponent : AppComponent,
    private val componentContext: ComponentContext,
    private val navBackListener : NavBackListener,
    private val systemId : String,
    private val waypointId : String,
    private val shipId : String?
) : Component,
    ComponentContext by componentContext,
    NavMarketDialogListener {

    @Inject
    lateinit var viewModel:  MarketViewModel

    private val dialogNavigation = OverlayNavigation<DialogConfig>()

    init {
        appComponent.inject(this)
        viewModel.navBackListener = navBackListener
        viewModel.navMarketDialogListener = this
    }

    private val _dialog =
        childOverlay(
            source = dialogNavigation,
            handleBackButton = true,
        ) { config, componentContext ->
            return@childOverlay when (config) {
                is DialogConfig.BuySellCargoDialog -> {
                    BuySellCargoDialogComponent(
                        appComponent = appComponent,
                        componentContext = componentContext,
                        config = BuySellCargoDialogConfig(
                            onDismissed = {
                                dialogNavigation.dismiss()
                                viewModel.update()
                            },
                            systemId = config.systemId,
                            waypointId = config.waypointId,
                            shipId = config.shipId,
                            cargoId = config.cargoId
                        )
                    )
                }
            }
        }

    @Composable
    override fun render() {

        val scope = rememberCoroutineScope()
        LaunchedEffect(viewModel) {
            viewModel.navBackListener = navBackListener

            viewModel.init(scope, systemId, waypointId, shipId)
        }

        MarketPage(
            viewModel = viewModel
        )

        _dialog.subscribeAsState().value.overlay?.let {
            (it.instance as? Component)?.render()
        }
    }

    override fun openBuySellCargoDialog(systemId : String, waypointId : String, shipId : String, cargoId : String) {
        dialogNavigation.activate(DialogConfig.BuySellCargoDialog(
            systemId = systemId,
            waypointId = waypointId,
            shipId = shipId,
            cargoId = cargoId
        ))
    }

    private sealed class DialogConfig : Parcelable {
        data class BuySellCargoDialog(
            val systemId : String,
            val waypointId : String,
            val shipId : String,
            val cargoId : String
        ) : DialogConfig()
    }
}