package com.jaehl.spaceTraders.di.modules

import com.jaehl.spaceTraders.data.repo.MarketRepo
import com.jaehl.spaceTraders.data.repo.MarketRepoImp
import com.jaehl.spaceTraders.data.repo.SystemRepo
import com.jaehl.spaceTraders.data.repo.SystemRepoImp
import dagger.Module
import dagger.Provides

@Module
class RepoModule {

    @Provides
    fun SystemRepo(systemRepoImp  : SystemRepoImp) : SystemRepo {
        return systemRepoImp
    }

    @Provides
    fun MarketRepo(marketRepoImp  : MarketRepoImp) : MarketRepo {
        return marketRepoImp
    }
}