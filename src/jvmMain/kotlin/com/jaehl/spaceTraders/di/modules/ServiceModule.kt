package com.jaehl.spaceTraders.di.modules

import com.jaehl.spaceTraders.data.services.*
import dagger.Module
import dagger.Provides

@Module
class ServiceModule {

    @Provides
    fun agentService(agentService : AgentServiceImp) : AgentService {
        return agentService
    }

    @Provides
    fun feetService(feetService : FleetServiceImp) : FleetService {
        return feetService
    }

    @Provides
    fun systemService(systemService : SystemServiceImp) : SystemService {
        return systemService
    }

    @Provides
    fun contractService(contractServiceImp : ContractServiceImp) : ContractService {
        return contractServiceImp
    }
}