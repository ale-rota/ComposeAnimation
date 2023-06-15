package com.alerota.composeanimation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
internal fun Header() {
    val logoSizeInDp = 60
    val logoSizeInPx = with(LocalDensity.current) { logoSizeInDp.dp.toPx() }

    Box(
        modifier = Modifier
            .background(Color.Transparent)
            .fillMaxWidth()
            .height(500.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    val size = this.size
                    val path = Path().apply {
                        addRoundRect(
                            RoundRect(
                                rect = Rect(
                                    offset = Offset(0f,  logoSizeInPx / 2),
                                    size = size,
                                ),
                            )
                        )
                    }
                    drawPath(path, color = Color.Yellow)
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(Modifier.size(logoSizeInDp.dp).background(Color.Green))
            Box(Modifier.size(200.dp).background(Color.Red))
        }
    }
}

@Preview
@Composable
private fun PwHeader() {
    Header()
}
