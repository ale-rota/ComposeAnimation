package com.alerota.composeanimation.util

import androidx.compose.ui.unit.dp
import com.alerota.composeanimation.ui.theme.Spacings

val centralElementLateralMargin = Spacings.spaceS

// Percentage of drag range during which scale actually changes
const val END_ANIMATION_PERCENTAGE = .2f
const val START_ANIMATION_PERCENTAGE = .5f

internal val CENTRAL_ELEMENT_COLLAPSED_HEIGHT_DP = 36.dp
internal val CENTRAL_ELEMENT_EXPANDED_HEIGHT_DP = 48.dp

// Max scale of the sticky element
const val MAX_SCALE = 1f