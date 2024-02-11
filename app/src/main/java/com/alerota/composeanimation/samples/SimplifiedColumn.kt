package com.alerota.composeanimation.samples

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SimplifiedColumn(
    content: @Composable () -> Unit
) {
    Layout(
        modifier = Modifier
            .background(Color.Yellow)
            .padding(20.dp),
        content = content
    ) { measurables, constraints ->
        // Track the Y coordinate we have placed children up to
        var yOffset = 0

        // Measure each child
        val placeables = measurables.map {
            it.measure(constraints)
        }

        // Set the size of the layout
        layout(constraints.maxWidth, constraints.maxHeight) {
            // Place each child at its calculated position
            placeables.forEach { placeable ->
                if (yOffset < constraints.maxHeight) {
                    placeable.place(0, yOffset)
                    yOffset += placeable.height
                }
            }
        }
    }

}

@Preview
@Composable
fun PwLayout() {
    val list = List(100) { "Item $it" }
    SimplifiedColumn {
        list.forEach { item ->
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(Color.Blue)
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = item
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
