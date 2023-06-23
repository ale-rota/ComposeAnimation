package com.alerota.composeanimation.header

import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alerota.composeanimation.R
import com.alerota.composeanimation.ui.component.Toolbar
import kotlin.math.roundToInt

enum class States {
    COLLAPSED,
    EXPANDED
}

private val IMAGE_HEIGHT_DP = 290.dp
private val TOOLBAR_TOP_MARGIN_DP = 20.dp

private const val STICKY_ELEMENT_Z_INDEX = 5f
private const val TOOLBAR_Z_INDEX = 4f
private const val TOP_CURTAIN_Z_INDEX = 3f
private const val BODY_Z_INDEX = 2f
private const val OPACITY_LAYER_Z_INDEX = 1f

private const val SWIPE_ANIMATION_DURATION_MILLIS = 600

private enum class SlotsEnum {
    Toolbar,
    TopCurtain,
    StickyElement,
    Body,
    Image,
    OpacityLayer,
    InfoBlock
}

/**
 * A scaffold containing the elements of the Store Wall.
 * It can scroll from [States.EXPANDED] to [States.COLLAPSED].
 * When swiping up, the [body] is initially dragged up until the [stickyElement] on top of it
 * reaches the [toolbar] position. From that moment the drag is interrupted and the scroll event
 * is consumed by the scrollable element inside the [body].
 *
 * @param title title of the Store Wall.
 * @param subLine short description of the Store Wall.
 * @param backgroundImage image placed as background.
 * @param toolbar horizontal set of UI elements shown on top of the composition.
 * @param stickyElement UI element shown on top of the bottom scrolling part (body). When swiping
 * up with the finger, the [stickyElement] moves up together with the [body], until it reaches
 * the y position of the [toolbar]. In that moment it stops moving and starts acting as a sticky
 * element. During the scroll, it also shrinks, so it can fit the empty space between the lateral
 * elements of the [toolbar].
 * @param body bottom scrolling UI element of the composition. It generally contains a
 * scrollable element (e.g. [LazyColumn]). When swiping up with the finger, it's initially dragged up. When it reaches
 * the [States.COLLAPSED] position, it stops being dragged up and the internal scrollable element
 * starts scrolling instead.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StoreWallScaffold(
    title: String,
    subLine: String,
    backgroundImage: String,
    toolbar: @Composable () -> Unit,
    stickyElement: @Composable () -> Unit,
    body: @Composable (scrollState: LazyGridState, offset: Int) -> Unit
) {
    val swipeableState = rememberSwipeableState(
        initialValue = States.EXPANDED,
        animationSpec = TweenSpec(durationMillis = SWIPE_ANIMATION_DURATION_MILLIS)
    )
    val scrollState = rememberLazyGridState()

    // Manages how much of the scroll offset must be consumed for dragging and how much for
    // scrolling.
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
            toolbar()
        }.map { it.measure(constraints) }

        val toolbarHeight = toolbarPlaceables.fold(0) { currentMax, placeable ->
            maxOf(currentMax, placeable.height)
        }

        val collapsedOffsetPx = TOOLBAR_TOP_MARGIN_DP.roundToPx() + toolbarHeight / 2
        val expandedOffsetPx = IMAGE_HEIGHT_DP.toPx()

        val topCurtainPlaceables = subcompose(SlotsEnum.TopCurtain) {
            // When in collapsed position, the curtain height completely covers the toolbar
            TopCurtain(height = TOOLBAR_TOP_MARGIN_DP + toolbarHeight.toDp())
        }.map { it.measure(constraints) }

        val imagePlaceables = subcompose(SlotsEnum.Image) {
            Image(
                painter = painterResource(id = R.drawable.food),
                contentDescription = "",
                modifier = Modifier
                    .height(IMAGE_HEIGHT_DP)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop,
                alignment = Alignment.BottomEnd
            )
        }.map { it.measure(constraints) }

        val opacityLayerPlaceables = subcompose(SlotsEnum.OpacityLayer) {
            OpacityLayer(
                modifier = Modifier
                    .height(IMAGE_HEIGHT_DP)
                    .fillMaxWidth(),
                swipeableState = swipeableState,
                expandedOffsetPx = expandedOffsetPx
            )
        }.map { it.measure(constraints) }

        val infoBlockPlaceables = subcompose(SlotsEnum.InfoBlock) {
            InfoBlock(
                title = title,
                subLine = subLine,
                swipeableState = swipeableState,
                collapsedOffsetPx = collapsedOffsetPx.toFloat(),
                expandedOffsetPx = expandedOffsetPx
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
                    collapsedOffsetPx = collapsedOffsetPx.toFloat(),
                    expandedOffsetPx = expandedOffsetPx,
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
                            collapsedOffsetPx.toFloat() to States.COLLAPSED,
                            expandedOffsetPx to States.EXPANDED,
                        )
                    )
                    .nestedScroll(connection)
            ) {
                body(scrollState = scrollState, offset = swipeableState.offset.value.roundToInt())
            }
        }.map { it.measure(constraints) }

        layout(0, 0) {
            toolbarPlaceables.forEach {
                it.placeRelative(
                    x = 0,
                    y = TOOLBAR_TOP_MARGIN_DP.roundToPx(),
                    zIndex = TOOLBAR_Z_INDEX
                )
            }
            topCurtainPlaceables.forEach {
                it.placeRelative(
                    x = 0,
                    y = -swipeableState.offset.value.roundToInt() + collapsedOffsetPx,
                    zIndex = TOP_CURTAIN_Z_INDEX
                )
            }
            imagePlaceables.forEach { it.placeRelative(0, 0) }
            opacityLayerPlaceables.forEach { it.placeRelative(0, 0, OPACITY_LAYER_Z_INDEX) }
            infoBlockPlaceables.forEach {
                it.placeRelative(
                    x = 12.dp.roundToPx(),
                    y = TOOLBAR_TOP_MARGIN_DP.roundToPx() + toolbarHeight + 30
                )
            }
            stickyElementPlaceables.forEach { it.placeRelative(0, 0, STICKY_ELEMENT_Z_INDEX) }
            bodyPlaceables.forEach {
                it.placeRelative(
                    x = 0,
                    y = swipeableState.offset.value.roundToInt(),
                    zIndex = BODY_Z_INDEX
                )
            }
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
            title = "Groceries",
            subLine = "Get your orders delivered in minutes!",
            backgroundImage = "",
            toolbar = {
                Toolbar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                )
            },
            stickyElement = {
                Box(
                    modifier = Modifier
                        .height(50.dp)
                        .width(350.dp)
                        .clip(RoundedCornerShape(36.dp))
                        .background(Color.Green)
                )
            },
            body = { scrollState, bottomPadding ->
                LazyVerticalGrid(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Magenta),
                    state = scrollState,
                    columns = GridCells.Fixed(1),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(
                        bottom = with(LocalDensity.current) { bottomPadding.toDp() }
                    )
                ) {
                    item { Spacer(modifier = Modifier.height(60.dp)) }

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

