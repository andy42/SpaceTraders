package com.jaehl.spaceTraders.di.modules

import com.jaehl.spaceTraders.util.Logger
import com.jaehl.spaceTraders.util.LoggerImp
import dagger.Module
import dagger.Provides

@Module
class UtilModule {

    @Provides
    fun logger(logger : LoggerImp) : Logger {
        return logger
    }
}