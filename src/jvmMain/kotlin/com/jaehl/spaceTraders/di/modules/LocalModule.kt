package com.jaehl.spaceTraders.di.modules

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jaehl.spaceTraders.data.local.*
import com.jaehl.spaceTraders.data.model.*
import com.jaehl.spaceTraders.util.Logger
import dagger.Module
import dagger.Provides

@Module
class LocalModule {

    @Provides
    fun LocalFileConfig(localFileConfigImp  : LocalFileConfigImp) : LocalFileConfig{
        return localFileConfigImp
    }

    @Provides
    fun cargoDataLoader(logger: Logger) : ObjectListLoader<CargoData> {
        return ObjectListJsonLoader<CargoData>(logger, object : TypeToken<Array<CargoData>>() {}.type)
    }

    @Provides
    fun marketHistoryLoader(logger: Logger, gson: Gson) : ObjectLoader<MarketHistory> {
        return ObjectLoaderImp<MarketHistory>(logger, gson, object : TypeToken<MarketHistory>() {}.type)
    }

    @Provides
    fun systemDataLoader(logger: Logger) : ObjectListLoader<StarSystem> {
        return ObjectListJsonLoader<StarSystem>(logger, object : TypeToken<Array<StarSystem>>() {}.type)
    }

    @Provides
    fun shipyardSavedLoader(logger: Logger) : ObjectListJsonLoader<ShipyardSaved> {
        return ObjectListJsonLoader<ShipyardSaved>(logger, object : TypeToken<Array<ShipyardSaved>>() {}.type)
    }

    @Provides
    fun agentsLocalLoader(logger: Logger) : ObjectListJsonLoader<AgentLocal> {
        return ObjectListJsonLoader<AgentLocal>(logger, object : TypeToken<Array<AgentLocal>>() {}.type)
    }
}