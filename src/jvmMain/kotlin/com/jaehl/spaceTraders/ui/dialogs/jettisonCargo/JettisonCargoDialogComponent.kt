package com.jaehl.spaceTraders.ui.dialogs.jettisonCargo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.arkivanov.decompose.ComponentContext
import com.jaehl.spaceTraders.di.AppComponent
import com.jaehl.spaceTraders.ui.navigation.Component
import javax.inject.Inject

data class JettisonCargoDialogConfig(
    val onDismissed : () -> Unit,
    val shipId : String,
    val cargoId : String
)

class JettisonCargoDialogComponent(
    appComponent : AppComponent,
    private val componentContext: ComponentContext,
    private val config : JettisonCargoDialogConfig
) : Component, ComponentContext by componentContext {

    @Inject
    lateinit var viewModel : JettisonCargoDialogViewModel

    init {
        appComponent.inject(this)
    }

    @Composable
    override fun render() {

        val scope = rememberCoroutineScope()
        LaunchedEffect(viewModel) {
            viewModel.init(scope, config)
        }

        JettisonCargoDialog(
            viewModel = viewModel
        )
    }
}