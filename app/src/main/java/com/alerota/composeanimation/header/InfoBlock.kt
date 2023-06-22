package com.alerota.composeanimation.header

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.material.Text
import androidx.compose.material.rememberSwipeableState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alerota.composeanimation.util.normalize

private const val MIN_OFFSET = 450f
private const val MIN_SCALE = .9f
private const val MAX_SCALE = 1f

/**
 * Small block composed by a [title] and a [subLine].
 * It shrinks according to the [swipeableState] value.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun InfoBlock(
    modifier: Modifier = Modifier,
    title: String,
    subLine: String,
    swipeableState: SwipeableState<States>,
    collapsedOffsetPx: Float,
    expandedOffsetPx: Float
) {

    val scale = swipeableState.offset.value.coerceAtLeast(MIN_OFFSET).normalize(
        oldMin = collapsedOffsetPx,
        oldMax = expandedOffsetPx,
        newMin = MIN_SCALE,
        newMax = MAX_SCALE,
    )

    Column(
        modifier = modifier
            .width(240.dp)
            .scale(scale),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = title,
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 2
        )
        Text(
            text = subLine,
            color = Color.White,
            fontSize = 18.sp,
            maxLines = 2
        )

    }
}

@OptIn(ExperimentalMaterialApi::class)
@Preview
@Composable
fun InfoBlockPw() {
    Box(modifier = Modifier.background(Color.Black)) {
        InfoBlock(
            modifier = Modifier.width(200.dp),
            title = "Groceries",
            subLine = "Get your orders delivered in minutes!",
            swipeableState = rememberSwipeableState(initialValue = States.COLLAPSED),
            collapsedOffsetPx = 400f,
            expandedOffsetPx = 1000f
        )
    }
}
