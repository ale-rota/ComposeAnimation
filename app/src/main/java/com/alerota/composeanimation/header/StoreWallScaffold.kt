package com.alerota.composeanimation.header

import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlin.math.roundToInt

enum class States {
    EXPANDED,
    COLLAPSED
}

const val TOOLBAR_MARGIN_TOP_PX = 100f
const val TOOLBAR_HEIGHT_PX = 140f

const val EXPANDED_OFFSET = TOOLBAR_MARGIN_TOP_PX + TOOLBAR_HEIGHT_PX / 2
const val COLLAPSED_OFFSET_PX = 800f

const val STICKY_ELEMENT_HEIGHT_PX = 200f

private const val BOTTOM_ELEMENT_Z_INDEX = 4f
private const val TOOLBAR_Z_INDEX = 3f
private const val TOP_CURTAIN_Z_INDEX = 2f
private const val BODY_Z_INDEX = 1f

private const val SWIPE_ANIMATION_DURATION_MILLIS = 600

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
                println("onPreScroll ${available.y}")
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
                println("onPostScroll ${available.y}")
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

    Box {

        TopCurtain(
            modifier = Modifier.offset {
                IntOffset(
                    0,
                    -swipeableState.offset.value.roundToInt() + EXPANDED_OFFSET.roundToInt()
                )
            },
            zIndex = TOP_CURTAIN_Z_INDEX
        )

        toolbar(modifier = Modifier.zIndex(TOOLBAR_Z_INDEX))

        BackgroundImage(
            modifier = Modifier
                .height(400.dp)
                .fillMaxWidth(),
            swipeableState = swipeableState
        )

        StickyElementContainer(
            modifier = Modifier
                .offset {
                    IntOffset(
                        0,
                        swipeableState.offset.value.roundToInt() - (STICKY_ELEMENT_HEIGHT_PX / 2).roundToInt()
                    )
                }
                .zIndex(BOTTOM_ELEMENT_Z_INDEX),
            swipeableState = swipeableState,
            stickyElement = stickyElement
        )

        body(
            modifier = Modifier
                .fillMaxWidth()
                .swipeable(
                    state = swipeableState,
                    orientation = Orientation.Vertical,
                    anchors = mapOf(
                        EXPANDED_OFFSET to States.EXPANDED,
                        COLLAPSED_OFFSET_PX to States.COLLAPSED,
                    )
                )
                .nestedScroll(connection)
                .offset {
                    IntOffset(
                        0,
                        swipeableState.offset.value.roundToInt()
                    )
                }
                .zIndex(BODY_Z_INDEX)
                .background(Color.Magenta),
            scrollState = scrollState
        )

    }
}


