package com.alerota.composeanimation.util

fun Float.normalize(oldMin: Float, oldMax: Float, newMin: Float, newMax: Float): Float {
    val initial = this.coerceAtLeast(oldMin).coerceAtMost(oldMax)
    val oldValueRange = oldMax - oldMin
    val newValueRange = newMax - newMin
    return ((initial - oldMin) / oldValueRange) * newValueRange + newMin
}