package com.alerota.composeanimation.scaffold.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

@Composable
fun BackgroundImage(modifier: Modifier) {
    Box(
        modifier = modifier
            .background(Color.Green)
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = "Header Background",
            fontSize = 20.sp
        )
    }
}