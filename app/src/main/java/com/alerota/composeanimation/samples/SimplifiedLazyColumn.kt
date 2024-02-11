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
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SimplifiedLazyColumn(
    content: @Composable () -> Unit
) {
    SubcomposeLayout(
        modifier = Modifier
            .background(Color.Yellow)
            .padding(20.dp)
    ) { constraints ->
        // Track the Y coordinate we have placed children up to
        var yOffset = 0

        // Measure each child
        val placeables = subcompose(SlotsEnum.Main, content).map {
            it.measure(constraints)
        }

        // Set the size of the layout
        layout(constraints.maxWidth, constraints.maxHeight) {
            // Place each child at its calculated position
            placeables.forEach { placeable ->
                if (yOffset < constraints.maxHeight) {
                    placeable.placeRelative(0, yOffset)
                    yOffset += placeable.height
                }
            }
        }
    }

}

@Preview
@Composable
fun PwSubComposeLayout() {
    val list = List(100) { "Item $it" }
    SimplifiedLazyColumn {
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

enum class SlotsEnum {
    Main
}