package com.jaehl.spaceTraders.ui.dialogs.registration

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.arkivanov.decompose.ComponentContext
import com.jaehl.spaceTraders.di.AppComponent
import com.jaehl.spaceTraders.ui.navigation.Component
import javax.inject.Inject

data class RegistrationDialogConfig(
    val onDismissed : () -> Unit
)

class RegistrationDialogComponent(
    appComponent : AppComponent,
    private val componentContext: ComponentContext,
    private val config : RegistrationDialogConfig
) : Component, ComponentContext by componentContext {

    @Inject
    lateinit var viewModel : RegistrationDialogViewModel

    init {
        appComponent.inject(this)
    }

    @Composable
    override fun render() {

        val scope = rememberCoroutineScope()
        LaunchedEffect(viewModel) {
            viewModel.init(scope, config)
        }

        RegistrationDialog(
            viewModel = viewModel
        )
    }
}