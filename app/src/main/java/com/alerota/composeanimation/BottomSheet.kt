package com.alerota.composeanimation

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.material.Text
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlin.math.roundToInt

enum class States {
    EXPANDED,
    COLLAPSED
}

private const val TOOLBAR_MARGIN_TOP_PX = 100f
private const val TOOLBAR_HEIGHT_PX = 140f
private const val TOP_CURTAIN_HEIGHT_PX = TOOLBAR_MARGIN_TOP_PX + TOOLBAR_HEIGHT_PX + 30

const val EXPANDED_OFFSET = TOOLBAR_MARGIN_TOP_PX + TOOLBAR_HEIGHT_PX / 2
const val COLLAPSED_OFFSET_PX = 800f
private const val DRAG_RANGE = COLLAPSED_OFFSET_PX - EXPANDED_OFFSET
const val ANIMATION_START_OFFSET = EXPANDED_OFFSET + DRAG_RANGE * 0.3f
private const val MIN_SCALE = .7f
private const val MAX_SCALE = 1f

private const val BOTTOM_ELEMENT_HEIGHT_PX = 200f

private const val BOTTOM_ELEMENT_Z_INDEX = 4f
private const val TOOLBAR_Z_INDEX = 3f
private const val TOP_CURTAIN_Z_INDEX = 2f
private const val BODY_Z_INDEX = 1f

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FullHeightBottomSheet(
    header: @Composable (modifier: Modifier) -> Unit,
    body: @Composable (modifier: Modifier) -> Unit
) {
    val swipeableState = rememberSwipeableState(initialValue = States.COLLAPSED)

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

            private fun Float.toOffset() = Offset(0f, this)
        }
    }

    BoxWithConstraints {

        TopCurtain(
            modifier = Modifier.offset {
                IntOffset(
                    0,
                    -swipeableState.offset.value.roundToInt() + EXPANDED_OFFSET.roundToInt()
                )
            },
            zIndex = TOP_CURTAIN_Z_INDEX
        )

        Toolbar(zIndex = TOOLBAR_Z_INDEX)

        BackgroundImage(
            modifier = Modifier
                .height(400.dp)
                .fillMaxWidth(),
            swipeableState = swipeableState
        )

        // Sticking search bar
        Box(
            Modifier
                .offset {
                    IntOffset(
                        0,
                        swipeableState.offset.value.roundToInt() - (BOTTOM_ELEMENT_HEIGHT_PX / 2).roundToInt()
                    )
                }
                .zIndex(BOTTOM_ELEMENT_Z_INDEX)
        ) {
            SearchBar(
                modifier = Modifier.zIndex(BOTTOM_ELEMENT_Z_INDEX),
                swipeableState = swipeableState,
                header = header
            )
        }

        // List
        Box(
            Modifier
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
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                contentAlignment = Alignment.TopCenter
            ) {
                body(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Magenta)
                )
            }
        }
    }
}

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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchBar(
    modifier: Modifier,
    swipeableState: SwipeableState<States>,
    header: @Composable (modifier: Modifier) -> Unit,
) {
    val headerScale by remember {
        derivedStateOf {
            val currentOffset = swipeableState.offset.value
            val scale = if (currentOffset < ANIMATION_START_OFFSET) {
                val oldValueRange = ANIMATION_START_OFFSET - EXPANDED_OFFSET
                val newValueRange = MAX_SCALE - MIN_SCALE
                ((currentOffset - EXPANDED_OFFSET) / oldValueRange) * newValueRange + MIN_SCALE

            } else MAX_SCALE
            return@derivedStateOf scale
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        header(
            modifier = Modifier
                .height(with(LocalDensity.current) { BOTTOM_ELEMENT_HEIGHT_PX.toDp() })
                .width(350.dp)
                .scale(headerScale)
                .zIndex(BOTTOM_ELEMENT_Z_INDEX)
        )
    }
}

@Composable
fun TopCurtain(modifier: Modifier, zIndex: Float) {
    Box(
        modifier
            .fillMaxWidth()
            .height(with(LocalDensity.current) { TOP_CURTAIN_HEIGHT_PX.toDp() })
            .zIndex(zIndex)
            .shadow(zIndex.dp)
            .background(Color.White)
    )
}

@Preview
@Composable
fun SheetPw() {
    FullHeightBottomSheet(
        header = {
            Box(
                modifier = it
                    .clip(RoundedCornerShape(36.dp))
                    .background(Color.Green)

            )
        },
        body = {
            LazyColumn(modifier = it) {
                //TODO put inside FullHeightBottomSheet
                item { Spacer(modifier = Modifier.height(120.dp)) }

                // List items
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
