package com.jaehl.spaceTraders.ui.pages.shipDetails

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.dp
import com.jaehl.spaceTraders.extensions.secondsFromNow
import com.jaehl.spaceTraders.ui.R
import com.jaehl.spaceTraders.ui.component.AppBar
import com.jaehl.spaceTraders.ui.component.HorizontalDivider
import com.jaehl.spaceTraders.ui.component.ItemChip
import com.jaehl.spaceTraders.ui.component.WaypointIcon
import com.jaehl.spaceTraders.ui.pages.shipDetails.viewModel.*
import kotlinx.coroutines.delay
import java.util.Date
import kotlin.time.Duration.Companion.seconds

@Composable
fun ShipDetailsPage(
    viewModel : ShipDetailsViewModel
) {
    LaunchedEffect(viewModel.isCoolingDown.value) {
        while (viewModel.isCoolingDown.value) {
            delay(1.seconds)
            viewModel.coolingDownSeconds.value = ((viewModel.coolingDownExpiration.value.time - Date().time) /1000).toInt()
        }
    }
    LaunchedEffect(viewModel.isInTransit.value) {

        while (viewModel.isInTransit.value) {
            delay(1.seconds)
            viewModel.secondsTillArrival.value = ((viewModel.shipViewModel.value.nav.arrival.time - Date().time) / 1000).toInt()
        }
    }


    Box {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(MaterialTheme.colors.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppBar(
                title = "ShipDetails",
                returnButton = true,
                onBackClick = {
                    viewModel.onBackClick()
                }
            )
            Column(modifier = Modifier
                .padding(20.dp)
                .width(800.dp)
            ) {
                ShipDetails(
                    modifier = Modifier,
                    viewModel = viewModel,
                    shipViewModel = viewModel.shipViewModel.value
                )
                Cargo(
                    modifier = Modifier,
                    viewModel = viewModel,
                    cargoViewModel = viewModel.shipViewModel.value.cargo
                )
                Mining(
                    modifier = Modifier,
                    viewModel = viewModel,
                    miningViewModel = viewModel.miningViewModel.value,
                    isCoolingDown = viewModel.isCoolingDown.value
                )
                MiningSurvey(
                    modifier = Modifier,
                    viewModel = viewModel,
                    miningSurveyViewModel = viewModel.miningSurveyViewModel.value
                )
                ShipTask(
                    modifier = Modifier,
                    viewModel = viewModel,
                )
            }
        }
    }
}

@Composable
fun ShipDetails(
    modifier: Modifier,
    viewModel : ShipDetailsViewModel,
    shipViewModel : ShipViewModel
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = "name : ${shipViewModel.name}")
            ShipStatus(
                modifier = Modifier,
                viewModel = viewModel,
                statusViewModel = viewModel.shipViewModel.value.state
            )
            ShipFuel(
                modifier = Modifier,
                viewModel = viewModel,
                fuelViewModel = viewModel.shipViewModel.value.fuel
            )
            ShipNav(
                modifier = Modifier,
                viewModel = viewModel,
                navViewModel = viewModel.shipViewModel.value.nav
            )
        }
    }
}

@Composable
fun ShipStatus(
    modifier : Modifier,
    viewModel : ShipDetailsViewModel,
    statusViewModel : StatusViewModel
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = statusViewModel.state)
        Button(
            modifier = Modifier
                .padding(start = 10.dp),
            onClick = {
                viewModel.onStateActionClick(statusViewModel.actionState)
            },
            enabled = statusViewModel.enabled
        ) {
            Text(
                modifier = Modifier,
                text = statusViewModel.actionState.value
            )
        }
    }
}

@Composable
fun ShipFuel(
    modifier : Modifier,
    viewModel : ShipDetailsViewModel,
    fuelViewModel : FuelViewModel
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Fuel (${fuelViewModel.current} : ${fuelViewModel.capacity})")
        Button(
            modifier = Modifier
                .padding(start = 10.dp),
            onClick = {
                viewModel.onRefuelClick()
            },
            enabled = fuelViewModel.enabled
        ) {
            Text(
                modifier = Modifier,
                text = "Refuel"
            )
        }
    }
}
//

@Composable
fun ShipNav(
    modifier : Modifier,
    viewModel : ShipDetailsViewModel,
    navViewModel : NavViewModel
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        WaypointIcon(
            modifier = Modifier
                .width(30.dp)
                .height(30.dp),
            tint = MaterialTheme.colors.onSurface,
            waypointType = navViewModel.waypointType
        )
        Column(
            modifier = Modifier
                .padding(start = 20.dp)
        ) {
            Text(text = navViewModel.location)
            Text(text = navViewModel.locationType)
        }
        Button(
            modifier = Modifier
                .padding(start = 10.dp),
            onClick = {
                viewModel.navigateToClick()
            },
            enabled = !viewModel.isInTransit.value
        ) {
            Text(
                modifier = Modifier,
                text = "select Destination"
            )
        }
        Button(
            modifier = Modifier
                .padding(start = 10.dp),
            onClick = {
                viewModel.viewSystemClick()
            }
        ) {
            Text(
                modifier = Modifier,
                text = "View System"
            )
        }
        if(viewModel.isInTransit.value) {
            Text(
                modifier = Modifier
                    .padding(start = 10.dp),
                text = "${viewModel.secondsTillArrival.value}"
            )
        }
    }
}

@Composable
fun Cargo(
    modifier : Modifier,
    viewModel : ShipDetailsViewModel,
    cargoViewModel : CargoViewModel
) {

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
        ) {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cargo   ${cargoViewModel.units} : ${cargoViewModel.capacity}"
                )
                Button(
                    modifier = Modifier,
                    onClick = {
                        viewModel.onOpenMarketsClick()
                    }
                ) {
                    Text(
                        modifier = Modifier,
                        text = "Markets"
                    )
                }
            }

            cargoViewModel.inventory.forEachIndexed {  index, cargoItem ->
                CargoRow(viewModel, index, cargoItem)
                if (index != (cargoViewModel.inventory.size -1)){
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
fun CargoRow(
    viewModel : ShipDetailsViewModel,
    index : Int,
    cargoItem : CargoViewModel.CargoItemViewModel
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Image(
                bitmap = remember { useResource(cargoItem.icon) { loadImageBitmap(it) } },
                "",
                colorFilter = ColorFilter.tint(
                    MaterialTheme.colors.onSurface
                ),
                modifier = Modifier
                    .padding(start = 10.dp)
                    .width(30.dp)
                    .height(30.dp)
                    .align(alignment = Alignment.CenterVertically)
            )

            Column(
                modifier = Modifier
                    .padding(start = 20.dp)
            ) {
                Text(text = cargoItem.name)
                Text(text = "Units : ${cargoItem.units}")
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                modifier = Modifier
                    .padding(start = 10.dp),
                onClick = {
                    viewModel.onJettisonCargoClick(cargoItem.symbol)
                },
                enabled = cargoItem.canJettison
            ) {
                Text(
                    modifier = Modifier,
                    text = "Jettison"
                )
            }

            Button(
                modifier = Modifier
                    .padding(start = 10.dp),
                onClick = {
                    viewModel.refineCargoItemClick(cargoItem.symbol)
                },
                enabled = cargoItem.isRefinable
            ) {
                Text(
                    modifier = Modifier,
                    text = "Refine"
                )
            }
        }
    }
}

@Composable
fun Mining(
    modifier : Modifier,
    viewModel : ShipDetailsViewModel,
    miningViewModel : MiningViewModel,
    isCoolingDown: Boolean
) {
    if(!miningViewModel.isVisible) return
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 20.dp)
    ) {

        Column(
            modifier = Modifier
                .padding(20.dp)
        ) {
            Text(
                modifier = modifier,
                text = "Mining"
            )
            if(miningViewModel.yield != null) {
                Text(
                    modifier = modifier.padding(top = 10.dp),
                    text = "${miningViewModel.yield.cargoData.name} : ${miningViewModel.yield.units}"
                )
            }

            Button(
                modifier = Modifier
                    .padding(top = 10.dp),
                onClick = {
                    viewModel.startMiningClick()
                },
                enabled = miningViewModel.canMine && !viewModel.isCoolingDown.value
            ) {
                Text(
                    modifier = Modifier,
                    text = "Start Mining"
                )
            }
            if(viewModel.isCoolingDown.value) {
                Text(
                    modifier = modifier.padding(top = 10.dp),
                    text = "Cool Down : ${viewModel.coolingDownSeconds.value}"
                )
            }
        }
    }
}

@Composable
fun MiningSurvey(
    modifier : Modifier,
    viewModel : ShipDetailsViewModel,
    miningSurveyViewModel : MiningSurveyViewModel
) {
    if(!miningSurveyViewModel.isVisible) return
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 20.dp)
    ) {

        Column(
            modifier = Modifier
                .padding(20.dp)
        ) {
            Text(
                modifier = modifier,
                text = "Mining Survey"
            )

            miningSurveyViewModel.surveyResults.forEachIndexed { index, surveyResult ->

                Text(text = "Size : ${surveyResult.size.value}")
                Text(text = "Expiration : ${surveyResult.expiration.secondsFromNow()}")
                com.jaehl.spaceTraders.ui.component.FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    surveyResult.deposits.forEach { deposit ->
                        ItemChip(
                            modifier = Modifier,
                            name = deposit.symbol
                        )
                    }
                }
            }

            Button(
                modifier = Modifier
                    .padding(top = 10.dp),
                onClick = {
                    viewModel.startMiningSurveyClick()
                },
                enabled = miningSurveyViewModel.canScan && !viewModel.isCoolingDown.value
            ) {
                Text(
                    modifier = Modifier,
                    text = "Start Mining Survey"
                )
            }
            if(viewModel.isCoolingDown.value) {
                Text(
                    modifier = modifier.padding(top = 10.dp),
                    text = "Cool Down : ${viewModel.coolDownTick.value}"
                )
            }
        }
    }

}

@Composable
fun ShipTask(
    modifier : Modifier,
    viewModel : ShipDetailsViewModel,
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
            Button(
                modifier = Modifier
                    .padding(top = 10.dp),
                onClick = {
                    viewModel.startTaskClick()
                },
                enabled = viewModel.taskButtonEnabled.value
            ) {
                Text(
                    modifier = Modifier,
                    text = "Start Task"
                )
            }
            if (viewModel.isCoolingDown.value) {
                Text(
                    modifier = modifier.padding(top = 10.dp),
                    text = "Cool Down : ${viewModel.coolDownTick.value}"
                )
            }
        }
    }
}