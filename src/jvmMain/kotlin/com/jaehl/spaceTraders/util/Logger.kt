package com.jaehl.spaceTraders.util

import javax.inject.Inject

interface Logger {
    fun log(message : String)
    fun error(message : String)
}

class LoggerImp @Inject constructor(

) : Logger {
    override fun log(message : String) {
        println("Log : $message")
    }
    override fun error(message : String) {
        println("Log : $message")
    }
}
