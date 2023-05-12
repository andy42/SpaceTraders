package com.jaehl.spaceTraders.ui.dialogs.buySellCargo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.arkivanov.decompose.ComponentContext
import com.jaehl.spaceTraders.di.AppComponent
import com.jaehl.spaceTraders.ui.navigation.Component
import javax.inject.Inject

data class BuySellCargoDialogConfig(
    val onDismissed : () -> Unit,
    val systemId : String,
    val waypointId : String,
    val shipId : String,
    val cargoId : String
)

class BuySellCargoDialogComponent(
    appComponent : AppComponent,
    private val componentContext: ComponentContext,
    private val config : BuySellCargoDialogConfig
) : Component, ComponentContext by componentContext {

    @Inject
    lateinit var viewModel : BuySellCargoDialogViewModel

    init {
        appComponent.inject(this)
    }

    @Composable
    override fun render() {

        val scope = rememberCoroutineScope()
        LaunchedEffect(viewModel) {
            viewModel.init(scope, config)
        }

        BuySellCargoDialog(
            viewModel = viewModel
        )
    }
}