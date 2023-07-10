package com.jaehl.spaceTraders.util

import java.util.Date

interface DateHelper {
    fun getNow() : Date
    fun getNowPlusMilliseconds(milliseconds : Long) : Date
}

class DateHelperImp : DateHelper{
    override fun getNow() : Date {
        return Date()
    }

    override fun getNowPlusMilliseconds(milliseconds: Long) : Date{
        return Date(Date().time + milliseconds)
    }
}

class DateHelperMock : DateHelper{

    private var now : Date = Date(0)

    fun setNow(now : Date){
        this.now = now
    }

    fun advanceNowBy(milliseconds: Long){
        now = Date(now.time + milliseconds)
    }

    fun advanceNowBy(seconds: Int){
        now = Date(now.time + seconds*1000)
    }

    override fun getNow() : Date {
        return now
    }

    override fun getNowPlusMilliseconds(milliseconds: Long): Date {
        return Date(now.time + milliseconds)
    }
}