package com.jaehl.spaceTraders.ui.pages.system

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.arkivanov.decompose.ComponentContext
import com.jaehl.spaceTraders.di.AppComponent
import com.jaehl.spaceTraders.ui.navigation.Component
import com.jaehl.spaceTraders.ui.navigation.NavBackListener
import com.jaehl.spaceTraders.ui.navigation.NavSystemListener
import javax.inject.Inject

class SystemComponent(
    appComponent : AppComponent,
    private val componentContext: ComponentContext,
    navBackListener : NavBackListener,
    navSystemListener : NavSystemListener,
    private val systemId : String,
    private val shipId : String?,
    private val marketsOnly : Boolean,
    private val travelOptions : Boolean
) : Component, ComponentContext by componentContext {

    @Inject
    lateinit var viewModel:  SystemViewModel

    init {
        appComponent.inject(this)
        viewModel.navBackListener = navBackListener
        viewModel.navSystemListener =navSystemListener
    }

    @Composable
    override fun render() {

        val scope = rememberCoroutineScope()
        LaunchedEffect(viewModel) {
            viewModel.init(scope, systemId, shipId, marketsOnly, travelOptions)
        }

        SystemPage(
            viewModel = viewModel
        )
    }
}