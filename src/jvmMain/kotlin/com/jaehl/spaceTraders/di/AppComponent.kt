package com.jaehl.spaceTraders.di

import com.jaehl.spaceTraders.di.modules.LocalModule
import com.jaehl.spaceTraders.di.modules.ServiceModule
import com.jaehl.spaceTraders.di.modules.NetworkModule
import com.jaehl.spaceTraders.di.modules.UtilModule
import com.jaehl.spaceTraders.ui.dialogs.buySellCargo.BuySellCargoDialogComponent
import com.jaehl.spaceTraders.ui.dialogs.jettisonCargo.JettisonCargoDialogComponent
import com.jaehl.spaceTraders.ui.pages.homePage.HomePageComponent
import com.jaehl.spaceTraders.ui.pages.market.MarketComponent
import com.jaehl.spaceTraders.ui.pages.shipDetails.ShipDetailsComponent
import com.jaehl.spaceTraders.ui.pages.system.SystemComponent
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        NetworkModule::class,
        ServiceModule::class,
        UtilModule::class,
        LocalModule::class
    ]
)
interface AppComponent {
    fun inject(homePageComponent : HomePageComponent)
    fun inject(shipDetailsComponent : ShipDetailsComponent)
    fun inject(systemComponent : SystemComponent)
    fun inject(marketComponent : MarketComponent)

    fun inject(buySellCargoDialogComponent : BuySellCargoDialogComponent)
    fun inject(JettisonCargoDialogComponent : JettisonCargoDialogComponent)
}