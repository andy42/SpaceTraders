package com.jaehl.spaceTraders.ui.component

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.runtime.Composable

@Composable
fun DialogTitleBar(title: String, onClose : (() -> Unit)? = null){
    var navigationIcon : @Composable (() -> Unit)? = null
    TopAppBar(
        title = {
            Text(title, color = MaterialTheme.colors.onPrimary)
        },
        navigationIcon = navigationIcon,
        backgroundColor = MaterialTheme.colors.primary,
        actions = {
            if(onClose != null) {
                IconButton(content = {
                    Icon(Icons.Outlined.Close, "Close", tint = MaterialTheme.colors.onPrimary)
                }, onClick = {
                    onClose?.invoke()
                })
            }
        }
    )
}