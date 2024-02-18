package com.alerota.composeanimation.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource

/**
 *  Manages how much of the scroll offset must be consumed for dragging and how much for scrolling.
 */
@OptIn(ExperimentalFoundationApi::class)
class SwipeableNestedScrollConnection(
    private val anchoredDraggableState: AnchoredDraggableState<States>
) : NestedScrollConnection {

    override fun onPreScroll(
        available: Offset,
        source: NestedScrollSource
    ): Offset {
        val delta = available.y
        return if (delta < 0) {
            anchoredDraggableState.dispatchRawDelta(delta).toOffset()
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
        return anchoredDraggableState.dispatchRawDelta(delta).toOffset()
    }

    private fun Float.toOffset() = Offset(0f, this)
}
