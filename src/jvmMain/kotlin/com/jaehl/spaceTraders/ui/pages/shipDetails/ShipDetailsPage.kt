package com.jaehl.spaceTraders.ui.pages.shipDetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jaehl.spaceTraders.data.model.response.Cooldown
import com.jaehl.spaceTraders.ui.R
import com.jaehl.spaceTraders.ui.component.AppBar
import com.jaehl.spaceTraders.ui.component.HorizontalDivider
import com.jaehl.spaceTraders.util.Logger
import com.jaehl.spaceTraders.util.LoggerImp
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun ShipDetailsPage(
    viewModel : ShipDetailsViewModel
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
            }
        }
    }
}

@Composable
fun ShipDetails(
    modifier: Modifier,
    viewModel : ShipDetailsViewModel,
    shipViewModel : ShipDetailsViewModel.ShipViewModel
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
    statusViewModel : ShipDetailsViewModel.StatusViewModel
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
    fuelViewModel : ShipDetailsViewModel.FuelViewModel
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
    navViewModel : ShipDetailsViewModel.NavViewModel
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier
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
            enabled = navViewModel.enabled
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
    }
}

@Composable
fun Cargo(
    modifier : Modifier,
    viewModel : ShipDetailsViewModel,
    cargoViewModel : ShipDetailsViewModel.CargoViewModel
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
    cargoItem : ShipDetailsViewModel.CargoViewModel.CargoItemViewModel
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Column(
            modifier = Modifier
        ) {
            Text(text = cargoItem.name)
            Text(text = "Units : ${cargoItem.units}")
        }

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

@Composable
fun Mining(
    modifier : Modifier,
    viewModel : ShipDetailsViewModel,
    miningViewModel : ShipDetailsViewModel.MiningViewModel,
    isCoolingDown: Boolean
) {
    if(!miningViewModel.isVisible) return
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 20.dp)
    ) {
        LaunchedEffect(isCoolingDown) {
            while (viewModel.isCoolingDown.value) {
                delay(1.seconds)
                viewModel.coolDownTick.value -= 1
            }
        }

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
                enabled = miningViewModel.canMine && !miningViewModel.isCoolingDown
            ) {
                Text(
                    modifier = Modifier,
                    text = "Start Mining"
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