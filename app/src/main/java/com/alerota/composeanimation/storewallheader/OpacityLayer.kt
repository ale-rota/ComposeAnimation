package com.alerota.composeanimation.storewallheader


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.material.rememberSwipeableState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alerota.composeanimation.ui.States
import com.alerota.composeanimation.util.normalize

private const val MIN_OPACITY = .4f
private const val MAX_OPACITY = 1f

/**
 * Opacity layer.
 * The opacity changes according to the [swipeableState] value.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OpacityLayer(
    modifier: Modifier = Modifier,
    swipeableState: SwipeableState<States>,
    collapsedOffsetPx: Float,
    expandedOffsetPx: Float
) {
    val opacity = swipeableState.offset.value.normalize(
        oldMin = collapsedOffsetPx,
        oldMax = expandedOffsetPx,
        newMin = MIN_OPACITY,
        newMax = MAX_OPACITY,
    )

    Box(
        modifier = modifier
            .background(Color.Black.copy(alpha = 1f - opacity))
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Preview(showBackground = true)
@Composable
fun OpacityLayerPreview() {
    val swipeableState = rememberSwipeableState(initialValue = States.EXPANDED)
    OpacityLayer(
        modifier = Modifier.height(300.dp).fillMaxWidth(),
        swipeableState = swipeableState,
        collapsedOffsetPx = 400f,
        expandedOffsetPx = 800f
    )
}
