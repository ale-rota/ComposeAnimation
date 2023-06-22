package com.alerota.composeanimation.header

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.material.rememberSwipeableState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alerota.composeanimation.util.normalize

private const val MIN_OFFSET = 450f
private const val MIN_OPACITY = .4f
private const val MAX_OPACITY = 1f

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OpacityLayer(
    modifier: Modifier,
    swipeableState: SwipeableState<States>,
    expandedOffsetPx: Float
) {
    val opacity = swipeableState.offset.value.coerceAtLeast(MIN_OFFSET).normalize(
        oldMin = MIN_OFFSET,
        oldMax = expandedOffsetPx,
        newMin = MIN_OPACITY,
        newMax = MAX_OPACITY,
    )
    println("offset=${swipeableState.offset.value}  -> opacity=$opacity")

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 1f - opacity))
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Preview(showBackground = true)
@Composable
fun OpacityLayerPreview() {
    val swipeableState = rememberSwipeableState(initialValue = States.EXPANDED)
    OpacityLayer(
        modifier = Modifier.height(300.dp),
        swipeableState = swipeableState,
        expandedOffsetPx = 800f
    )
}