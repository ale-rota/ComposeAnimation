package com.alerota.composeanimation.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState

data class ShrinkableElementArguments @OptIn(ExperimentalFoundationApi::class) constructor(
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