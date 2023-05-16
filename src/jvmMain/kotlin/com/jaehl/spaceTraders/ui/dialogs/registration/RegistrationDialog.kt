package com.jaehl.spaceTraders.ui.dialogs.registration

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jaehl.spaceTraders.ui.R
import com.jaehl.spaceTraders.ui.component.DialogTitleBar

@Composable
fun RegistrationDialog(
    viewModel : RegistrationDialogViewModel
){
    Box(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .clickable {}
        .background(R.Color.dialogBackground)) {
        Column(
            modifier = Modifier
                .width(400.dp)
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
            OutlinedTextField(
                modifier = Modifier
                    .padding(top = 20.dp),
                label = { Text("Token") },
                value = viewModel.token.value,
                onValueChange = {
                    viewModel.onTokenChange(it)
                }
            )
            Button(
                modifier = Modifier
                    .padding(20.dp),
                onClick = {
                    viewModel.onCreateClick()
                },
                enabled = viewModel.createButtonEnabled.value
            ) {
                Text(
                    modifier = Modifier,
                    text = "Create"
                )
            }
        }
    }
}