package com.alerota.composeanimation.samples

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun LayoutModifierExample() {

    Box(modifier = Modifier.fillMaxWidth().height(500.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Red)
                    //.wei
        )


    }

