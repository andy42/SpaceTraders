package com.jaehl.spaceTraders.ui.pages.homePage

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
import com.jaehl.spaceTraders.ui.dialogs.registration.RegistrationDialogComponent
import com.jaehl.spaceTraders.ui.dialogs.registration.RegistrationDialogConfig
import com.jaehl.spaceTraders.ui.navigation.*
import javax.inject.Inject

interface NavHomePageDialogListener {
    fun openRegistrationDialog()
}

class HomePageComponent(
    appComponent : AppComponent,
    componentContext: ComponentContext,
    navBackListener : NavBackListener,
    navShipListener : NavShipListener,
    navSystemListener : NavSystemListener,
    navTaskListener: NavTaskListener
) : Component,
    ComponentContext by componentContext,
    NavHomePageDialogListener{

    @Inject
    lateinit var viewModel: HomeViewModel

    private val dialogNavigation = OverlayNavigation<DialogConfig>()

    private val _dialog =
        childOverlay(
            source = dialogNavigation,
            handleBackButton = true,
        ) { config, componentContext ->
            return@childOverlay when (config) {
                is DialogConfig.RegistrationDialog -> {
                    RegistrationDialogComponent(
                        appComponent = appComponent,
                        componentContext = componentContext,
                        config = RegistrationDialogConfig(
                            onDismissed = {
                                dialogNavigation.dismiss()
                                viewModel.requestDataUpdate()
                            }
                        )
                    )
                }
            }
        }

    init {
        appComponent.inject(this)
        viewModel.navBackListener = navBackListener
        viewModel.navShipListener = navShipListener
        viewModel.navSystemListener = navSystemListener
        viewModel.navHomePageDialogListener = this
        viewModel.navTaskListener = navTaskListener
    }

    @Composable
    override fun render() {
        val scope = rememberCoroutineScope()
        LaunchedEffect(viewModel) {
            viewModel.init(scope)
        }

        HomePage(
            viewModel = viewModel
        )

        _dialog.subscribeAsState().value.overlay?.let {
            (it.instance as? Component)?.render()
        }
    }

    override fun openRegistrationDialog() {
        dialogNavigation.activate(DialogConfig.RegistrationDialog)
    }

    private sealed class DialogConfig : Parcelable {
        object RegistrationDialog : DialogConfig()
    }
}