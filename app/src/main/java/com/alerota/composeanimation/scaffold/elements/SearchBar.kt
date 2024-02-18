package com.alerota.composeanimation.scaffold.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SearchBar(modifier: Modifier) {
    Box(
        modifier
            .width(300.dp)
            .height(200.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Red)
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = "Search bar",
            fontSize = 20.sp
        )
    }
}