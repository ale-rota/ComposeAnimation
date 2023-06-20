package com.alerota.composeanimation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.material.rememberSwipeableState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.alerota.composeanimation.util.normalize

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BackgroundImage(
    modifier: Modifier,
    swipeableState: SwipeableState<States>
) {
    val alpha = swipeableState.offset.value.normalize(
        oldMin = EXPANDED_OFFSET,
        oldMax = COLLAPSED_OFFSET_PX,
        newMin = 0f,
        newMax = 1f,

    )
    Box(modifier = modifier) {
        Image(
            painter = painterResource(id = R.drawable.food),
            contentDescription = "Your Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 1f - alpha)
                        )
                    )
                )
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Preview(showBackground = true)
@Composable
fun DarkOpacityImageLayoutPreview() {
    val swipeableState = rememberSwipeableState(initialValue = States.COLLAPSED)
    BackgroundImage(modifier = Modifier.fillMaxSize(), swipeableState = swipeableState)
}