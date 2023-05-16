package com.jaehl.spaceTraders.ui.pages.system

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jaehl.spaceTraders.ui.R
import com.jaehl.spaceTraders.ui.component.AppBar
import com.jaehl.spaceTraders.ui.component.HorizontalDivider
import com.jaehl.spaceTraders.ui.component.WaypointIcon
import kotlin.math.absoluteValue

@Composable
fun SystemPage(
    viewModel : SystemViewModel
) {
    Box {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(MaterialTheme.colors.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppBar(
                title = "System",
                returnButton = true,
                onBackClick = {
                    viewModel.onBackClick()
                }
            )
            Column(modifier = Modifier
                .padding(20.dp)
                .width(800.dp)
            ) {
                Waypoints(
                    modifier = Modifier,
                    viewModel = viewModel,
                    systemWaypoints =viewModel.systemWaypoints
                )
            }
        }
    }
}

@Composable
fun Waypoints(
    modifier: Modifier,
    viewModel : SystemViewModel,
    systemWaypoints : List<SystemViewModel.SystemWaypointViewModel>
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
        ) {
            systemWaypoints.forEachIndexed {  index, cargoItem ->
                Waypoint(
                    modifier = Modifier,
                    viewModel,
                    cargoItem)
                if (index != (systemWaypoints.size -1)){
                    HorizontalDivider(
                        modifier = Modifier.padding(top = 10.dp),
                        color = R.Color.neutral200
                    )
                }
            }
        }
    }
}

@Composable
fun Waypoint(
    modifier: Modifier,
    viewModel : SystemViewModel,
    systemWaypoint : SystemViewModel.SystemWaypointViewModel
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            WaypointIcon(
                modifier = Modifier
                    .width(40.dp)
                    .height(40.dp),
                tint = MaterialTheme.colors.onSurface,
                waypointType = systemWaypoint.type
            )
            Column(
                modifier = Modifier
                    .padding(start = 20.dp)
            ) {
                Text(text = systemWaypoint.symbol)
                Text(text = "Type : ${systemWaypoint.type.value}")
                if (systemWaypoint.distance != null) {
                    Text(text = "Distance : ${systemWaypoint.distance.absoluteValue}")
                }
            }
        }
        Row {
            if(systemWaypoint.hasMarketplace) {
                OutlinedButton(
                    modifier = Modifier
                        .padding(start = 10.dp),
                    onClick = {
                        viewModel.openMarket(systemWaypoint.symbol)
                    },
                ) {
                    Text(
                        modifier = Modifier,
                        text = "Open Market"
                    )
                }
            }
            if(systemWaypoint.hasShipyard) {
                OutlinedButton(
                    modifier = Modifier
                        .padding(start = 10.dp),
                    onClick = {
                        viewModel.openShipyard(systemWaypoint.symbol)
                    },
                ) {
                    Text(
                        modifier = Modifier,
                        text = "Open Shipyard"
                    )
                }
            }
            if(systemWaypoint.travelTo) {
                Button(
                    modifier = Modifier
                        .padding(start = 10.dp),
                    onClick = {
                        viewModel.travelTo(systemWaypoint.symbol)
                    }
                ) {
                    Text(
                        modifier = Modifier,
                        text = "Go To"
                    )
                }
            }
        }
    }
}