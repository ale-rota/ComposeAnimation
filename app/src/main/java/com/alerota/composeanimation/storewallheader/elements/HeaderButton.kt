package com.alerota.composeanimation.storewallheader.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun HeaderButton(modifier: Modifier, text: String) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Magenta)
    ) {
        Text(
            modifier = Modifier
                .padding(18.dp)
                .background(Color.Magenta),
            text = text,
            textAlign = TextAlign.Center
        )
    }
}