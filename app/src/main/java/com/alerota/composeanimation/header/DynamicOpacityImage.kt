package com.alerota.composeanimation.header

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.material.rememberSwipeableState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alerota.composeanimation.R
import com.alerota.composeanimation.util.normalize

private const val MIN_OFFSET = 450f
private const val MIN_OPACITY = .4f
private const val MAX_OPACITY = 1f

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DynamicOpacityImage(
    modifier: Modifier,
    swipeableState: SwipeableState<States>
) {
    val opacity = swipeableState.offset.value.coerceAtLeast(MIN_OFFSET).normalize(
        oldMin = MIN_OFFSET,
        oldMax = COLLAPSED_OFFSET_PX,
        newMin = MIN_OPACITY,
        newMax = MAX_OPACITY,
    )
    println("offset=${swipeableState.offset.value}  -> opacity=$opacity")

    Box(modifier = modifier) {
        Image(
            painter = painterResource(id = R.drawable.food),
            contentDescription = "Your Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit,
            alignment = Alignment.BottomEnd
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 1f - opacity))
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Preview(showBackground = true)
@Composable
fun DarkOpacityImageLayoutPreview() {
    val swipeableState = rememberSwipeableState(initialValue = States.COLLAPSED)
    DynamicOpacityImage(modifier = Modifier.size(400.dp), swipeableState = swipeableState)
}