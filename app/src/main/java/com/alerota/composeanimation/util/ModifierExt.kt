package com.alerota.composeanimation.util

import androidx.compose.ui.Modifier

internal fun Modifier.conditional(condition: Boolean, modifier: Modifier.() -> Modifier): Modifier {
    return if (condition) {
        then(modifier(Modifier))
    } else {
        this
    }
}