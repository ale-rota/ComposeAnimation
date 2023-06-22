package com.alerota.composeanimation.header

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

private const val BOTTOM_PADDING_PX = 30

@Composable
fun TopCurtain(
    modifier: Modifier,
    zIndex: Float,
    toolbarMarginTopPx: Int,
    toolbarHeightPx: Int
) {
    val topCurtainHeightPx = toolbarMarginTopPx + toolbarHeightPx + BOTTOM_PADDING_PX
    Box(
        modifier
            .fillMaxWidth()
            .height(with(LocalDensity.current) { topCurtainHeightPx.toDp() })
            .shadow(zIndex.dp)
            .background(Color.White)
    )
}