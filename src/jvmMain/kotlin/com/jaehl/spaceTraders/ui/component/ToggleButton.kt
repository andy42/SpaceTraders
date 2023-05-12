package com.jaehl.spaceTraders.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ToggleButton(
    modifier: Modifier,
    list : List<String>,
    value : Int,
    onValueChange : (value : Int) -> Unit
){
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = 2.dp,
                color = MaterialTheme.colors.primary
            ),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        list.forEachIndexed() { index, title ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(if (index == value) MaterialTheme.colors.primary else MaterialTheme.colors.surface)
                    .clickable {
                        onValueChange(index)
                    },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    color = if (index == value) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface,
                    textAlign = TextAlign.Center,
                    text = title
                )
            }
        }
    }
}