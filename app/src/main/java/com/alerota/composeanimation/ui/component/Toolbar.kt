package com.alerota.composeanimation.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.zIndex
import com.alerota.composeanimation.header.TOOLBAR_HEIGHT_PX
import com.alerota.composeanimation.header.TOOLBAR_MARGIN_TOP_PX

@Composable
fun Toolbar(zIndex: Float) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(with(LocalDensity.current) { TOOLBAR_HEIGHT_PX.toDp() })
            .offset(y = with(LocalDensity.current) { TOOLBAR_MARGIN_TOP_PX.toDp() })
            .zIndex(zIndex),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left element
        Box {
            Button(onClick = {}) { Text("Left") }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Right element
        Row(
            modifier = Modifier
        ) {
            Button(onClick = {}) { Text("Right") }
        }

    }
}