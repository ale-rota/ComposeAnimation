package com.alerota.composeanimation.header

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale

private const val MIN_SCALE = .7f
private const val MAX_SCALE = 1f


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StickyElementContainer(
    modifier: Modifier,
    swipeableState: SwipeableState<States>,
    stickyElement: @Composable (modifier: Modifier) -> Unit,
    collapsedOffsetPx: Float,
    expandedOffsetPx: Float
) {
    val dragRange = expandedOffsetPx - collapsedOffsetPx
    val animationStartOffset = collapsedOffsetPx + dragRange * 0.3f
    val headerScale by remember {
        derivedStateOf {
            val currentOffset = swipeableState.offset.value
            val scale = if (currentOffset < animationStartOffset) {
                val oldValueRange = animationStartOffset - collapsedOffsetPx
                val newValueRange = MAX_SCALE - MIN_SCALE
                ((currentOffset - collapsedOffsetPx) / oldValueRange) * newValueRange + MIN_SCALE

            } else MAX_SCALE
            return@derivedStateOf scale
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        stickyElement(
            modifier = Modifier
                .scale(headerScale)
        )
    }
}