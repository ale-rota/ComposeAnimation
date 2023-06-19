package com.alerota.composeanimation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlin.math.roundToInt

enum class States {
    EXPANDED,
    COLLAPSED
}

private const val TOOLBAR_MARGIN_TOP_PX = 100f
private const val TOOLBAR_HEIGHT_PX = 140f
private const val TOP_CURTAIN_HEIGHT_PX = TOOLBAR_MARGIN_TOP_PX + TOOLBAR_HEIGHT_PX + 50

private const val EXPANDED_OFFSET = TOOLBAR_MARGIN_TOP_PX - TOOLBAR_HEIGHT_PX / 2
private const val COLLAPSED_OFFSET_PX = 800f
private const val DRAG_RANGE = COLLAPSED_OFFSET_PX - EXPANDED_OFFSET
private const val ANIMATION_START_OFFSET = EXPANDED_OFFSET + DRAG_RANGE * 0.3f
private const val MIN_SCALE = .7f
private const val MAX_SCALE = 1f

private val BOTTOM_ELEMENT_HEIGHT_DP = 100.dp

private const val BOTTOM_ELEMENT_Z_INDEX = 5f
private const val TOOLBAR_Z_INDEX = 4f
private const val TOP_CURTAIN_Z_INDEX = 3f
private const val BODY_Z_INDEX = 2f
private const val BACKGROUND_IMAGE_Z_INDEX = 1f

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FullHeightBottomSheet(
    header: @Composable (modifier: Modifier) -> Unit,
    body: @Composable (modifier: Modifier) -> Unit
) {
    val swipeableState = rememberSwipeableState(initialValue = States.COLLAPSED)
    val headerScale by remember {
        derivedStateOf {
            val currentOffset = swipeableState.offset.value
            println("aaa offset: $currentOffset")
            val scale = if (currentOffset < ANIMATION_START_OFFSET) {
                val oldValueRange = ANIMATION_START_OFFSET - EXPANDED_OFFSET
                val newValueRange = MAX_SCALE - MIN_SCALE
                println("aaa calculated: ${((currentOffset - EXPANDED_OFFSET) / oldValueRange) * newValueRange + MIN_SCALE}")
                ((currentOffset - EXPANDED_OFFSET) / oldValueRange) * newValueRange + MIN_SCALE

            } else MAX_SCALE
            println("aaa scale: $scale")
            return@derivedStateOf scale
        }
    }
    val bottomElementHeightPx = with(LocalDensity.current) { BOTTOM_ELEMENT_HEIGHT_DP.toPx() }

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

        TopCurtain(modifier = Modifier.offset {
            println("curtain offset: ${- swipeableState.offset.value.roundToInt()}")
            IntOffset(
                0,
                - swipeableState.offset.value.roundToInt()
            )
        })

        Toolbar()

        Image(
            painterResource(R.drawable.food),
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .height(400.dp)
                .fillMaxWidth()
                .zIndex(0f)
        )

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
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .drawBehind {
                        val size = this.size
                        val path = Path().apply {
                            addRoundRect(
                                RoundRect(
                                    rect = Rect(
                                        offset = Offset(0f, bottomElementHeightPx / 2),
                                        size = size,
                                    )
                                )
                            )
                        }
                        drawPath(path, color = Color.Magenta)
                    },
                contentAlignment = Alignment.TopCenter
                //horizontalAlignment = Alignment.CenterHorizontally
            ) {
                println("offset: ${swipeableState.offset.value.roundToInt()}")
                header(
                    modifier = Modifier
                        .height(BOTTOM_ELEMENT_HEIGHT_DP)
                        .width(350.dp)
                        .scale(headerScale)
                        .zIndex(BOTTOM_ELEMENT_Z_INDEX)
                )
                body(modifier = Modifier.zIndex(BODY_Z_INDEX))
            }
        }
    }
}

@Composable
fun Toolbar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(with(LocalDensity.current) { TOOLBAR_HEIGHT_PX.toDp() })
            .offset(y = with(LocalDensity.current) { TOOLBAR_MARGIN_TOP_PX.toDp() })
            .zIndex(TOOLBAR_Z_INDEX)
            .border(2.dp, Color.Red),
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

@Composable
fun TopCurtain(modifier: Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .height(with(LocalDensity.current) { TOP_CURTAIN_HEIGHT_PX.toDp() })
            .zIndex(TOP_CURTAIN_Z_INDEX)
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
                    .background(Color.Green)
            )
        },
        body = {
            LazyColumn(modifier = it) {
                //TODO put inside FullHeightBottomSheet
                item { Spacer(modifier = Modifier.height(120.dp)) }

                // List items
                items(100) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .height(80.dp)
                    ) {
                        Text(
                            text = "Item $it",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

            }
        }
    )
}
