package com.jaehl.spaceTraders.ui.pages.homePage

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.arkivanov.decompose.ComponentContext
import com.jaehl.spaceTraders.di.AppComponent
import com.jaehl.spaceTraders.ui.navigation.Component
import com.jaehl.spaceTraders.ui.navigation.NavBackListener
import com.jaehl.spaceTraders.ui.navigation.NavShipListener
import javax.inject.Inject

class HomePageComponent(
    appComponent : AppComponent,
    private val componentContext: ComponentContext,
    private val navBackListener : NavBackListener,
    private val navShipListener : NavShipListener
) : Component, ComponentContext by componentContext {

    @Inject
    lateinit var viewModel: HomeViewModel

    init {
        appComponent.inject(this)
    }

    @Composable
    override fun render() {
        val scope = rememberCoroutineScope()
        LaunchedEffect(viewModel) {
            viewModel.navBackListener = navBackListener
            viewModel.navShipListener = navShipListener
            viewModel.init(scope)
        }

        HomePage(
            viewModel = viewModel
        )
    }
}