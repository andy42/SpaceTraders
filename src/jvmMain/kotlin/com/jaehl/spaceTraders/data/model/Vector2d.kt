package com.jaehl.spaceTraders.data.model
import kotlin.math.*

class Vector2d {
    val x : Float
    val y : Float
    constructor(x : Int, y: Int) {
        this.x = x.toFloat()
        this.y = y.toFloat()
    }
    constructor(x : Float, y: Float) {
        this.x = x
        this.y = y
    }
    fun distance(from: Vector2d): Float {
        return sqrt((from.y - this.y).pow(2) + (from.x - this.x).pow(2))
    }
}
