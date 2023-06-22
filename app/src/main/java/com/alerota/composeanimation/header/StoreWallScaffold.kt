package com.alerota.composeanimation.header

import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alerota.composeanimation.ui.component.Toolbar
import kotlin.math.roundToInt

enum class States {
    EXPANDED,
    COLLAPSED
}

private val IMAGE_HEIGHT_DP = 290.dp

private const val STICKY_ELEMENT_Z_INDEX = 4f
private const val TOOLBAR_Z_INDEX = 3f
private const val TOP_CURTAIN_Z_INDEX = 2f
private const val BODY_Z_INDEX = 1f

private const val SWIPE_ANIMATION_DURATION_MILLIS = 600

private enum class SlotsEnum {
    Toolbar,
    TopCurtain,
    StickyElement,
    Body,
    Image
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StoreWallScaffold(
    toolbar: @Composable (modifier: Modifier) -> Unit,
    stickyElement: @Composable (modifier: Modifier) -> Unit,
    body: @Composable (modifier: Modifier, scrollState: LazyListState) -> Unit
) {
    val swipeableState = rememberSwipeableState(
        initialValue = States.COLLAPSED,
        animationSpec = TweenSpec(durationMillis = SWIPE_ANIMATION_DURATION_MILLIS)
    )
    val scrollState = rememberLazyListState()

    val connection = remember {
        object : NestedScrollConnection {

            override fun onPreScroll(
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                val delta = available.y
                return if (delta < 0) {
                    swipeableState.performDrag(delta).toOffset()
                } else {
                    Offset.Zero
                }
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                val delta = available.y
                return swipeableState.performDrag(delta).toOffset()
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                return if (available.y < 0 &&
                    scrollState.firstVisibleItemIndex == 0 &&
                    scrollState.firstVisibleItemScrollOffset == 0
                ) {
                    swipeableState.performFling(available.y)
                    available
                } else {
                    Velocity.Zero
                }
            }

            override suspend fun onPostFling(
                consumed: Velocity,
                available: Velocity
            ): Velocity {
                swipeableState.performFling(velocity = available.y)
                return super.onPostFling(consumed, available)
            }


            private fun Float.toOffset() = Offset(0f, this)
        }
    }
    
    SubcomposeLayout { constraints ->
        val toolbarPlaceables = subcompose(SlotsEnum.Toolbar) {
            Box(Modifier) {
                toolbar(modifier = Modifier)
            }
        }.map { it.measure(constraints) }

        val toolbarHeight = toolbarPlaceables.fold(0) { currentMax, placeable ->
            maxOf(currentMax, placeable.height)
        }

        val expandedOffset = 0 + toolbarHeight / 2

        val topCurtainPlaceables = subcompose(SlotsEnum.TopCurtain) {
            TopCurtain(
                modifier = Modifier.offset {
                    IntOffset(
                        0,
                        -swipeableState.offset.value.roundToInt() + expandedOffset
                    )
                },
                zIndex = TOP_CURTAIN_Z_INDEX,
                toolbarMarginTopPx = 0,
                toolbarHeightPx = toolbarHeight,
            )
        }.map { it.measure(constraints) }

        val imagePlaceables = subcompose(SlotsEnum.Image) {
            DynamicOpacityImage(
                modifier = Modifier
                    .height(IMAGE_HEIGHT_DP)
                    .fillMaxWidth(),
                swipeableState = swipeableState, 
                collapsedOffsetPx = IMAGE_HEIGHT_DP.toPx(),
            )
        }.map { it.measure(constraints) }

        val stickyElementPlaceables = subcompose(SlotsEnum.StickyElement) {
            Box(Modifier) {
                StickyElementContainer(
                    modifier = Modifier
                        .layout { measurable, constraints ->
                            val placeable = measurable.measure(constraints)
                            layout(placeable.width, placeable.height) {
                                placeable.place(
                                    0,
                                    swipeableState.offset.value.roundToInt() - placeable.height / 2
                                )
                            }
                        },
                    swipeableState = swipeableState,
                    stickyElement = stickyElement,
                    expandedOffset = expandedOffset.toFloat(),
                    collapsedOffsetPx = IMAGE_HEIGHT_DP.toPx(),
                )
            }
        }.map { it.measure(constraints) }

        val bodyPlaceables = subcompose(SlotsEnum.Body) {
            Box(
                Modifier
                    .swipeable(
                        state = swipeableState,
                        orientation = Orientation.Vertical,
                        anchors = mapOf(
                            expandedOffset.toFloat() to States.EXPANDED,
                            IMAGE_HEIGHT_DP.toPx() to States.COLLAPSED,
                        )
                    )
                    .nestedScroll(connection)
            ) {
                body(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Magenta),
                    scrollState = scrollState
                )

            }
        }.map { it.measure(constraints) }


        layout(0, 0) {
            toolbarPlaceables.forEach { it.placeRelative(0, 0, TOOLBAR_Z_INDEX) }
            topCurtainPlaceables.forEach { it.placeRelative(0, 0, TOP_CURTAIN_Z_INDEX) }
            imagePlaceables.forEach { it.placeRelative(0, 0) }
            stickyElementPlaceables.forEach { it.placeRelative(0, 0, STICKY_ELEMENT_Z_INDEX) }
            bodyPlaceables.forEach { it.placeRelative(
                0,
                swipeableState.offset.value.roundToInt(),
                BODY_Z_INDEX
            ) }
        }
    }

}

@Preview
@Composable
fun SheetPw() {
    Column(
        Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        StoreWallScaffold(
            toolbar = { modifier ->
                Toolbar(
                    modifier = modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .offset(y = 0.dp)
                )
            },
            stickyElement = {
                Box(
                    modifier = it
                        .height(50.dp)
                        .width(350.dp)
                        .clip(RoundedCornerShape(36.dp))
                        .background(Color.Green)
                )
            },
            body = { modifier, scrollState ->
                LazyColumn(modifier = modifier, state = scrollState) {
                    item { Spacer(modifier = Modifier.height(120.dp)) }

                    items(50) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp, horizontal = 20.dp)
                                .height(80.dp),
                            backgroundColor = Color.LightGray,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "Item $it",
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxSize(),
                                fontSize = 20.sp,
                            )
                        }
                    }
                }
            }
        )
    }

}

