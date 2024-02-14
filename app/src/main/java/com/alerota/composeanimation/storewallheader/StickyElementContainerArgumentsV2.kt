package com.alerota.composeanimation.storewallheader

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import com.alerota.composeanimation.ui.States

@OptIn(ExperimentalMaterialApi::class)
data class StickyElementContainerArgumentsV2(
    val swipeableState: SwipeableState<States>,
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
