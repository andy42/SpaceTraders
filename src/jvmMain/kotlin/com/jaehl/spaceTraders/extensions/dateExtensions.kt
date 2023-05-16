package com.jaehl.spaceTraders.extensions

import java.util.Date

fun Date.secondsFromNow() : Int{
    return ((this.time - Date().time)/1000).toInt()
}