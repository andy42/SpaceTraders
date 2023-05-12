package com.jaehl.spaceTraders.ui.dialogs.buySellCargo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jaehl.spaceTraders.ui.R
import com.jaehl.spaceTraders.ui.component.DialogTitleBar
import com.jaehl.spaceTraders.ui.component.ToggleButton

@Composable
fun BuySellCargoDialog(
    viewModel : BuySellCargoDialogViewModel
){
    Box(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .background(R.Color.dialogBackground)) {
        Column(
            modifier = Modifier
                .width(600.dp)
                .padding(top = 20.dp, bottom = 20.dp)
                .align(Alignment.Center)
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DialogTitleBar(
                title = viewModel.title.value,
                onClose = {
                    viewModel.onCloseClick()
                }
            )

            if(viewModel.showTransactionTypePicker.value) {
                ToggleButton(
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .width(300.dp)
                        .height(40.dp),
                    list = listOf("Selling", "Buying"),
                    value = viewModel.transactionType.value.value,
                    onValueChange = {
                        viewModel.onTransactionTypeChange(
                            TransactionType.fromValue(it)
                        )
                    }
                )
            }

            SliderView(
                modifier = Modifier
                    .padding(top = 20.dp),
                viewModel.valueSlider.value,
                onValueChange = {
                    viewModel.onValueChange(it)
                }
            )

            Text(
                modifier = Modifier
                    .padding(top = 10.dp),
                text = viewModel.cargoName.value
            )

            if(viewModel.errorMessage.value.isError){
                Text(
                    modifier = Modifier
                        .padding(top = 10.dp),
                    text = viewModel.errorMessage.value.message
                )
            }

            Text(
                modifier = Modifier
                    .padding(top = 10.dp),
                text = "${viewModel.transactionType.value.name} price : ${viewModel.transactionViewModel.value.price}",
                textAlign = TextAlign.Center
            )
            Text(
                modifier = Modifier,
                text = "Quantity : ${viewModel.transactionViewModel.value.quantity}",
                textAlign = TextAlign.Center
            )
            Text(
                modifier = Modifier,
                text = "Total  : ${viewModel.transactionViewModel.value.total}",
                textAlign = TextAlign.Center
            )

            Button(
                modifier = Modifier
                    .padding(20.dp),
                onClick = {
                    viewModel.onActionClick()
                },
                enabled = !viewModel.errorMessage.value.isError
            ) {
                Text(
                    modifier = Modifier,
                    text = if(viewModel.transactionType.value == TransactionType.Buy) "Buy" else "Sell"
                )
            }

        }
    }
}

@Composable
fun SliderView(
    modifier : Modifier,
    sliderValue : SliderValue,
    onValueChange  : (value : Float) -> Unit
) {
    if(!sliderValue.show) return
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier
                .width(20.dp),
            text = sliderValue.minValue.toInt().toString(),
            textAlign = TextAlign.Center
        )

        Slider(
            modifier = Modifier
                .width(400.dp),
            valueRange = sliderValue.minValue..sliderValue.maxValue,
            value = sliderValue.currentValue,
            onValueChange = onValueChange
        )

        Text(
            modifier = Modifier
                .width(20.dp),
            text = sliderValue.maxValue.toInt().toString(),
            textAlign = TextAlign.Center

        )
    }
}