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
import androidx.compose.ui.zIndex

const val TOP_CURTAIN_HEIGHT_PX = TOOLBAR_MARGIN_TOP_PX + TOOLBAR_HEIGHT_PX + 30

@Composable
fun TopCurtain(modifier: Modifier, zIndex: Float) {
    Box(
        modifier
            .fillMaxWidth()
            .height(with(LocalDensity.current) { TOP_CURTAIN_HEIGHT_PX.toDp() })
            .zIndex(zIndex)
            .shadow(zIndex.dp)
            .background(Color.White)
    )
}