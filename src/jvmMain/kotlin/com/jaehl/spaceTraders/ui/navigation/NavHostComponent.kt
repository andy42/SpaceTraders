package com.jaehl.spaceTraders.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.essenty.parcelable.Parcelable
import com.jaehl.spaceTraders.data.repo.AgentRepo
import com.jaehl.spaceTraders.data.services.AuthService
import com.jaehl.spaceTraders.di.AppComponent
import com.jaehl.spaceTraders.di.DaggerAppComponent
import com.jaehl.spaceTraders.ui.pages.homePage.HomePageComponent
import com.jaehl.spaceTraders.ui.pages.market.MarketComponent
import com.jaehl.spaceTraders.ui.pages.shipDetails.ShipDetailsComponent
import com.jaehl.spaceTraders.ui.pages.system.SystemComponent
import com.jaehl.spaceTraders.ui.pages.systemSearch.SystemSearchComponent
import javax.inject.Inject

interface NavBackListener {
    fun navigateBack()
}

interface NavShipListener {
    fun openShipDetails(shipId : String)
}

interface NavSystemListener {
    fun openSystemDetails(systemId : String, shipId : String?, marketsOnly : Boolean, travelOptions : Boolean)
    fun openMarket(systemId : String, waypointId : String, shipId : String?)
    fun openSystemSearch()
}

class NavHostComponent(
    componentContext: ComponentContext,
) : Component,
    ComponentContext by componentContext,
    NavBackListener,
    NavShipListener,
    NavSystemListener
{
    private val appComponent: AppComponent = DaggerAppComponent.create()

    private val navigation = StackNavigation<ScreenConfig>()

    init {
        appComponent.inject(this)
    }

    private val _childStack =
        childStack(
            source = navigation,
            initialConfiguration = ScreenConfig.Home,
            handleBackButton = true, // Pop the back stack on back button press
            childFactory = ::createScreenComponent,
        )

    private fun createScreenComponent(
        screenConfig: ScreenConfig,
        componentContext: ComponentContext
    ): Component {
        return when (screenConfig) {
            is ScreenConfig.Home -> HomePageComponent(
                appComponent = appComponent,
                componentContext = componentContext,
                navBackListener = this,
                navShipListener = this,
                navSystemListener = this
            )
            is ScreenConfig.ShipDetails -> ShipDetailsComponent(
                appComponent = appComponent,
                componentContext = componentContext,
                navBackListener = this,
                navSystemListener = this,
                shipId = screenConfig.shipId
            )
            is ScreenConfig.SystemDetails -> SystemComponent(
                appComponent = appComponent,
                componentContext = componentContext,
                navBackListener = this,
                navSystemListener = this,
                systemId = screenConfig.systemId,
                shipId = screenConfig.shipId,
                marketsOnly = screenConfig.marketsOnly,
                travelOptions = screenConfig.travelOptions
            )
            is ScreenConfig.Market -> MarketComponent(
                appComponent = appComponent,
                componentContext = componentContext,
                navBackListener = this,
                systemId = screenConfig.systemId,
                waypointId = screenConfig.waypointId,
                shipId = screenConfig.shipId
            )
            is ScreenConfig.SystemSearch -> SystemSearchComponent(
                appComponent = appComponent,
                componentContext = componentContext,
                navBackListener = this
            )
        }
    }

    override fun navigateBack() {
        navigation.pop()
    }

    override fun openShipDetails(shipId: String) {
        navigation.push(ScreenConfig.ShipDetails(
            shipId = shipId
        ))
    }

    override fun openSystemDetails(systemId: String, shipId : String?, marketsOnly : Boolean, travelOptions : Boolean) {
        navigation.push(ScreenConfig.SystemDetails(
            systemId = systemId,
            shipId= shipId,
            marketsOnly = marketsOnly,
            travelOptions = travelOptions

        ))
    }

    override fun openSystemSearch() {
        navigation.push(ScreenConfig.SystemSearch)
    }

    override fun openMarket(systemId : String, waypointId : String, shipId : String?) {
        navigation.push(ScreenConfig.Market(
            systemId = systemId,
            waypointId = waypointId,
            shipId= shipId
        ))
    }

    @OptIn(ExperimentalDecomposeApi::class)
    @Composable
    override fun render() {
        Children(stack = _childStack, modifier = Modifier){
            it.instance.render()
        }
    }

    private sealed class ScreenConfig : Parcelable {
        object Home : ScreenConfig()
        data class ShipDetails(
            val shipId : String
        ) : ScreenConfig()
        data class SystemDetails(
            val systemId : String,
            val shipId : String?,
            val marketsOnly : Boolean,
            val travelOptions : Boolean
        ) : ScreenConfig()

        data class Market(
            val systemId : String,
            val waypointId : String,
            val shipId : String?
        ) : ScreenConfig()
        object SystemSearch : ScreenConfig()
    }
}

