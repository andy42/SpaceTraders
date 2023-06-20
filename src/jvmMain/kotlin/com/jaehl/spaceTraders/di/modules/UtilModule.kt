package com.jaehl.spaceTraders.di.modules

import com.jaehl.spaceTraders.util.*
import dagger.Module
import dagger.Provides

@Module
class UtilModule {

    @Provides
    fun logger(logger : LoggerImp) : Logger {
        return logger
    }

    @Provides
    fun dateHelper() : DateHelper {
        return DateHelperImp()
    }
}