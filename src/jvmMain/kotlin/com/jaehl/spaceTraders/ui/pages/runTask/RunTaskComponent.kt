package com.jaehl.spaceTraders.ui.pages.runTask

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.arkivanov.decompose.ComponentContext
import com.jaehl.spaceTraders.di.AppComponent
import com.jaehl.spaceTraders.ui.navigation.Component
import com.jaehl.spaceTraders.ui.navigation.NavBackListener
import javax.inject.Inject

class RunTaskComponent(
    appComponent : AppComponent,
    private val componentContext: ComponentContext,
    private val navBackListener : NavBackListener
) : Component, ComponentContext by componentContext {

    @Inject
    lateinit var viewModel:  RunTaskViewModel

    init {
        appComponent.inject(this)
    }

    @Composable
    override fun render() {

        val scope = rememberCoroutineScope()
        LaunchedEffect(viewModel) {
            viewModel.navBackListener = navBackListener

            viewModel.init(scope)
        }

        RunTaskPage(
            viewModel = viewModel
        )
    }
}