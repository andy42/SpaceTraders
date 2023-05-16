package com.jaehl.spaceTraders.ui.pages.market

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jaehl.spaceTraders.ui.R
import com.jaehl.spaceTraders.ui.component.AppBar
import com.jaehl.spaceTraders.ui.component.FlowCrossAxisAlignment
import com.jaehl.spaceTraders.ui.component.HorizontalDivider
import com.jaehl.spaceTraders.ui.component.ItemChip

@Composable
fun MarketPage(
    viewModel : MarketViewModel
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
                title = "Market",
                returnButton = true,
                onBackClick = {
                    viewModel.onBackClick()
                }
            )
            Column(modifier = Modifier
                .padding(20.dp)
                .width(800.dp)
            ) {
                Section(
                    modifier = Modifier,
                    title = "Imports",
                    description = "List of goods that are sought as imports in this market",
                    items = viewModel.imports
                )
                Section(
                    modifier = Modifier,
                    title = "Exports",
                    description = "List of goods that are exported from this market",
                    items = viewModel.exports
                )
                TradeGoodsSection(
                    modifier = Modifier,
                    viewModel = viewModel,
                    title = "TradeGoods",
                    items = viewModel.tradeGoods
                )
            }
        }
    }
}

@Composable
fun RowDivider(
    modifier : Modifier,
    index : Int,
    size : Int
) {
    if (index != (size -1)){
        HorizontalDivider(
            modifier = modifier,
            color = R.Color.neutral200
        )
    }
}

@Composable
fun Section(
    modifier : Modifier,
    title : String,
    description : String,
    items : List<String>
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
            Text(text = title)
            Text(text = description, modifier = Modifier
                .padding(top = 10.dp))
            com.jaehl.spaceTraders.ui.component.FlowRow(
                modifier = Modifier
                    .padding(top = 10.dp),
                crossAxisAlignment = FlowCrossAxisAlignment.Center) {
                items.forEachIndexed { index, item ->
                    ItemChip(
                        modifier = Modifier,
                        name = item
                    )
                }
            }
        }

    }
}

@Composable
fun TradeGoodsSection(
    modifier : Modifier,
    viewModel : MarketViewModel,
    title : String,
    items : List<MarketItemViewModel>
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
            Text(text = title)

            Row(
                modifier = Modifier
                    .padding(top = 20.dp, bottom = 10.dp)
            ) {
                Text(
                    modifier = modifier
                        .weight(1f),
                    text = "Name"
                )
                Text(
                    modifier = modifier
                        .weight(1f),
                    text = "Sell"
                )
                Text(
                    modifier = modifier
                        .weight(1f),
                    text = "Buy"
                )
            }
            items.forEachIndexed { index, marketItemViewModel ->
                HorizontalDivider(
                    modifier = modifier,
                    color = R.Color.neutral200
                )
                MarketItem(
                    modifier = Modifier,
                    viewModel = viewModel,
                    item =  marketItemViewModel
                )
            }
        }
    }
}

@Composable
fun MarketItem(
    modifier : Modifier,
    viewModel : MarketViewModel,
    item : MarketItemViewModel
) {
    Row(
        modifier = Modifier
            .clickable {
                viewModel.onBuySellClick(item.symbol)
            }
            .padding(top = 10.dp, bottom = 10.dp)
    ) {
        Text(
            modifier = modifier
                .weight(1f),
            text = item.name
        )
        Text(
            modifier = modifier
                .weight(1f),
            text = item.sellPrice.toString()
        )
        Text(
            modifier = modifier
                .weight(1f),
            text = item.purchasePrice.toString()
        )
    }
}
