package com.jaehl.spaceTraders.ui.component

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import com.jaehl.spaceTraders.data.model.WaypointType

@Composable
fun WaypointIcon(
    modifier : Modifier,
    tint : Color,
    waypointType : WaypointType
){
    Image(
        bitmap =  useResource(
            when(waypointType){
                WaypointType.planet -> "planet.png"
                WaypointType.gasGiant -> "gasGiant.png"
                WaypointType.moon -> "moon.png"
                WaypointType.orbitalStation -> "spaceStation.png"
                WaypointType.jumpGate -> "unknown.png"
                WaypointType.asteroidField -> "asteroids.png"
                WaypointType.nebula -> "nebula.png"
                WaypointType.debrisField -> "unknown.png"
                WaypointType.gravityWell -> "unknown.png"

            }
        ) { loadImageBitmap(it) } ,
        "",
        colorFilter = ColorFilter.tint(
            tint
        ),
        modifier = modifier
    )
}