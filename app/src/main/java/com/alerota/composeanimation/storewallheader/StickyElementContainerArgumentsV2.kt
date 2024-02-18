package com.alerota.composeanimation.storewallheader

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import com.alerota.composeanimation.ui.States

data class StickyElementContainerArgumentsV2 @OptIn(ExperimentalFoundationApi::class) constructor(
    val anchoredDraggableState: AnchoredDraggableState<States>,
    val horizontalMargins: Int,
    val xStart: Int,
    val xEnd: Int,
    val screenWidth: Int,
    val dragRange: Float,
    val yOffset: Int,
    val centralElementExpandedHeightPx: Float,
    val centralElementCollapsedHeightPx: Float,
    val centralElementLateralMargin: Int,
    val currentOffset: Float
)
