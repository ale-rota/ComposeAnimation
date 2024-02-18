package com.alerota.composeanimation.scaffold.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun TopCurtain(
    modifier: Modifier = Modifier,
    height: Dp = 0.dp,
) {
    Box(
        modifier
            .fillMaxWidth()
            .height(height)
            .shadow(10.dp)
            .background(Color.White)
    )
}
