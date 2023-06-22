package com.alerota.composeanimation.header

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

private val BOTTOM_PADDING_DP = 6.dp

@Composable
fun TopCurtain(height: Dp) {
    val topCurtainHeight = height + BOTTOM_PADDING_DP
    Box(
        Modifier
            .fillMaxWidth()
            .height(topCurtainHeight)
            .shadow(10.dp)
            .background(Color.White)
    )
}