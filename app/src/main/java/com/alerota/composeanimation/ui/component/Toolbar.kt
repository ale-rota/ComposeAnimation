package com.alerota.composeanimation.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun Toolbar(modifier: Modifier) {
    Row(
        modifier = modifier,
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