package com.alerota.composeanimation.ui

import androidx.compose.material.ExperimentalMaterialApi
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.SwipeableState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource

/**
 *  Manages how much of the scroll offset must be consumed for dragging and how much for scrolling.
 */
@OptIn(ExperimentalMaterialApi::class)
class SwipeableNestedScrollConnection(
    private val swipeableState: SwipeableState<States>
) : NestedScrollConnection {

    override fun onPreScroll(
        available: Offset,
        source: NestedScrollSource
    ): Offset {
        val delta = available.y
        return if (delta < 0) {
            swipeableState.performDrag(delta).toOffset()
        } else {
            Offset.Zero
        }
    }

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource
    ): Offset {
        val delta = available.y
        return swipeableState.performDrag(delta).toOffset()
    }

    private fun Float.toOffset() = Offset(0f, this)
}
