package com.jaehl.spaceTraders.ui.pages.homePage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.dp
import com.jaehl.spaceTraders.ui.component.AppBar
import com.jaehl.spaceTraders.ui.component.ship.ShipRow
import com.jaehl.spaceTraders.ui.component.ship.ShipViewModel

@Composable
fun HomePage(
    viewModel : HomeViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(MaterialTheme.colors.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppBar(
            title = "Home",
            returnButton = false,
            onBackClick = {
                viewModel.onBackClick()
            }
        )
        Column(modifier = Modifier
            .padding(20.dp)
            .width(800.dp)
        ) {
            AgentDetails(
                modifier = Modifier
                    .padding(top = 10.dp),
                viewModel = viewModel
            )
            Button(
                modifier = Modifier
                    .padding(start = 10.dp, top = 10.dp),
                onClick = {
                    viewModel.openSystemSearchClick()
                }
            ) {
                Text(
                    modifier = Modifier,
                    text = "System Search"
                )
            }

            Button(
                modifier = Modifier
                    .padding(start = 10.dp, top = 10.dp),
                onClick = {
                    viewModel.onRunTaskClick()
                }
            ) {
                Text(
                    modifier = Modifier,
                    text = "Run Task"
                )
            }

            Ships(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 10.dp),
                viewModel = viewModel,
                ships = viewModel.ships
            )
        }
    }
}

@Composable
fun AgentDetails(
    modifier: Modifier,
    viewModel : HomeViewModel
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = "Username : ${viewModel.userName.value}")
        }
    }
}

@Composable
fun Ships(
    modifier: Modifier,
    viewModel : HomeViewModel,
    ships : List<ShipViewModel>
) {
    val rocketBitmap = remember { useResource("rocket.png") { loadImageBitmap(it) } }
    LazyColumn(
        modifier = modifier
    ) {
        itemsIndexed(ships) { index, ship ->
            ShipRow(
                index = index,
                ship = ship,
                rocketBitmap = rocketBitmap,
                onShipClick = {
                    viewModel.onShipClick(it)
                }
            )
        }
    }
}