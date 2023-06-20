package com.jaehl.spaceTraders.di

import com.jaehl.spaceTraders.di.modules.*
import com.jaehl.spaceTraders.ui.dialogs.buySellCargo.BuySellCargoDialogComponent
import com.jaehl.spaceTraders.ui.dialogs.jettisonCargo.JettisonCargoDialogComponent
import com.jaehl.spaceTraders.ui.dialogs.registration.RegistrationDialogComponent
import com.jaehl.spaceTraders.ui.navigation.NavHostComponent
import com.jaehl.spaceTraders.ui.pages.homePage.HomePageComponent
import com.jaehl.spaceTraders.ui.pages.market.MarketComponent
import com.jaehl.spaceTraders.ui.pages.runTask.RunTaskComponent
import com.jaehl.spaceTraders.ui.pages.shipDetails.ShipDetailsComponent
import com.jaehl.spaceTraders.ui.pages.system.SystemComponent
import com.jaehl.spaceTraders.ui.pages.systemSearch.SystemSearchComponent
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        NetworkModule::class,
        ServiceModule::class,
        UtilModule::class,
        LocalModule::class,
        RepoModule::class
    ]
)
interface AppComponent {
    fun inject(navHostComponent : NavHostComponent)

    fun inject(homePageComponent : HomePageComponent)
    fun inject(shipDetailsComponent : ShipDetailsComponent)
    fun inject(systemComponent : SystemComponent)
    fun inject(marketComponent : MarketComponent)
    fun inject(systemSearchComponent : SystemSearchComponent)
    fun inject(runTaskComponent : RunTaskComponent)

    fun inject(buySellCargoDialogComponent : BuySellCargoDialogComponent)
    fun inject(jettisonCargoDialogComponent : JettisonCargoDialogComponent)
    fun inject(registrationDialogComponent : RegistrationDialogComponent)
}