package com.jaehl.spaceTraders.di.modules

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jaehl.spaceTraders.data.local.*
import com.jaehl.spaceTraders.data.model.CargoData
import com.jaehl.spaceTraders.data.model.MarketHistory
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
}