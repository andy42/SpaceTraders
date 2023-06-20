package com.jaehl.spaceTraders.ui.component.ship

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp

@Composable
fun ShipRow(
    index : Int,
    ship : ShipViewModel,
    rocketBitmap : ImageBitmap,
    onShipClick : (shipId : String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
    ) {
        Row(
            modifier = Modifier
                .clickable { onShipClick(ship.shipId) }
                .padding(10.dp)
            ,
            //.background(if(index.mod(2) == 0) R.Color.rowBackgroundEven else R.Color.rowBackgroundOdd),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                bitmap = rocketBitmap,
                "",
                colorFilter = ColorFilter.tint(
                    MaterialTheme.colors.onSurface
                ),
                modifier = Modifier
                    .padding(start = 10.dp)
                    .width(20.dp)
                    .height(20.dp)
                    .align(alignment = Alignment.CenterVertically)
            )
            Text(
                ship.name,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp)
            )
            Text(
                ship.state,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp)
            )
            Text(
                ship.fuel,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp)
            )

            Text(
                ship.cargo,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp)
            )
        }
    }
}