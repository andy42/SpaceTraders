package com.jaehl.spaceTraders.ui.pages.shipDetails

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
import com.jaehl.spaceTraders.ui.dialogs.jettisonCargo.JettisonCargoDialogComponent
import com.jaehl.spaceTraders.ui.dialogs.jettisonCargo.JettisonCargoDialogConfig
import com.jaehl.spaceTraders.ui.navigation.Component
import com.jaehl.spaceTraders.ui.navigation.NavBackListener
import com.jaehl.spaceTraders.ui.navigation.NavSystemListener
import com.jaehl.spaceTraders.ui.pages.market.MarketComponent
import javax.inject.Inject

interface NavShipDetailsDialogListener {
    fun openJettisonCargoDialog(shipId : String, cargoId : String)
}

class ShipDetailsComponent(
    appComponent : AppComponent,
    private val componentContext: ComponentContext,
    private val navBackListener : NavBackListener,
    private val navSystemListener : NavSystemListener,
    private val shipId : String
) : Component,
    ComponentContext by componentContext,
    NavShipDetailsDialogListener{

    @Inject
    lateinit var viewModel:  ShipDetailsViewModel

    private val dialogNavigation = OverlayNavigation<DialogConfig>()

    init {
        appComponent.inject(this)
        viewModel.navBackListener = navBackListener
        viewModel.navSystemListener = navSystemListener
        viewModel.navShipDetailsDialogListener = this
    }

    private val _dialog =
        childOverlay(
            source = dialogNavigation,
            handleBackButton = true,
        ) { config, componentContext ->
            return@childOverlay when (config) {
                is DialogConfig.JettisonCargoDialog -> {
                    JettisonCargoDialogComponent(
                        appComponent = appComponent,
                        componentContext = componentContext,
                        config = JettisonCargoDialogConfig(
                            onDismissed = {
                                dialogNavigation.dismiss()
                                viewModel.requestDataUpdate()
                            },
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

            viewModel.init(scope, shipId)
        }

        ShipDetailsPage(
            viewModel = viewModel
        )

        _dialog.subscribeAsState().value.overlay?.let {
            (it.instance as? Component)?.render()
        }
    }

    override fun openJettisonCargoDialog(shipId: String, cargoId: String) {
        dialogNavigation.activate(
            DialogConfig.JettisonCargoDialog(
            shipId = shipId,
            cargoId = cargoId
        ))
    }

    private sealed class DialogConfig : Parcelable {
        data class JettisonCargoDialog(
            val shipId : String,
            val cargoId : String
        ) : DialogConfig()
    }
}