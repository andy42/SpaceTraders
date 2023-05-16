package com.jaehl.spaceTraders.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun ItemChip(
    modifier : Modifier,
    name : String
) {
    Row(
        modifier = modifier
            .padding(top = 5.dp, end = 10.dp, bottom = 5.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colors.primary)
            .padding(start = 10.dp, top = 5.dp, bottom = 5.dp, end = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(
            text = name,
            color = MaterialTheme.colors.onPrimary,
            modifier = Modifier
                .padding(start = 5.dp)
        )
    }
}