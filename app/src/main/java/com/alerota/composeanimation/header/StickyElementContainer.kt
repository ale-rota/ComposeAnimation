package com.alerota.composeanimation.header

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StickyElementContainer(
    modifier: Modifier,
    swipeableState: SwipeableState<States>,
    stickyElement: @Composable (modifier: Modifier) -> Unit,
) {
    val headerScale by remember {
        derivedStateOf {
            val currentOffset = swipeableState.offset.value
            val scale = if (currentOffset < ANIMATION_START_OFFSET) {
                val oldValueRange = ANIMATION_START_OFFSET - EXPANDED_OFFSET
                val newValueRange = MAX_SCALE - MIN_SCALE
                ((currentOffset - EXPANDED_OFFSET) / oldValueRange) * newValueRange + MIN_SCALE

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
                .height(with(LocalDensity.current) { BOTTOM_ELEMENT_HEIGHT_PX.toDp() })
                .width(350.dp)
                .scale(headerScale)
                .zIndex(BOTTOM_ELEMENT_Z_INDEX)
        )
    }
}