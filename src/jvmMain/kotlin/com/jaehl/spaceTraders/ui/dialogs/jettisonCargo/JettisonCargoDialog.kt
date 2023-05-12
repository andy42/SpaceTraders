package com.jaehl.spaceTraders.ui.dialogs.jettisonCargo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.jaehl.spaceTraders.ui.dialogs.buySellCargo.SliderValue
import com.jaehl.spaceTraders.ui.dialogs.buySellCargo.TransactionType

@Composable
fun JettisonCargoDialog(
    viewModel : JettisonCargoDialogViewModel
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
            Text(
                modifier = Modifier
                    .padding(top = 10.dp),
                text = viewModel.jettisonAmountText.value
            )

            Button(
                modifier = Modifier
                    .padding(20.dp),
                onClick = {
                    viewModel.onJettisonClick()
                }
            ) {
                Text(
                    modifier = Modifier,
                    text = "Jettison"
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