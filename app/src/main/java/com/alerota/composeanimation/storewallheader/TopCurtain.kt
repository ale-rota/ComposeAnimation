package com.alerota.composeanimation.storewallheader

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
fun TopCurtain(height: Dp) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(height)
            .shadow(10.dp)
            .background(Color.White)
    )
}
