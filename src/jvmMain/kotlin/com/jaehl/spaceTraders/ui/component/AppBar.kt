package com.jaehl.spaceTraders.ui.component

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun AppBar(
    title: String,
    returnButton: Boolean = false,
    onBackClick : () -> Unit
) {
    var navigationIcon : @Composable (() -> Unit)? = null
    if(returnButton) navigationIcon = {
        IconButton(content = {
            Icon(Icons.Outlined.ArrowBack, "back", tint = Color.White)
        }, onClick = {
            onBackClick()
        })
    }

    TopAppBar(
        title = {
            Text(title)
        },
        navigationIcon = navigationIcon,
        actions = {

        }
    )
}