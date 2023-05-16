package com.jaehl.spaceTraders.ui.pages.systemSearch

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jaehl.spaceTraders.ui.component.AppBar

@Composable
fun SystemSearchPage(
    viewModel : SystemSearchViewModel
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
                title = "SystemSearch",
                returnButton = true,
                onBackClick = {
                    viewModel.onBackClick()
                }
            )
            Column(modifier = Modifier
                .padding(20.dp)
                .width(800.dp)
            ) {
                Text("NEW Page")
            }
        }
    }
}