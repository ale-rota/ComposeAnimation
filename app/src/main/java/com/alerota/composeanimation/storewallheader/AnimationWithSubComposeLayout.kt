package com.alerota.composeanimation.storewallheader

import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.material.Text
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alerota.composeanimation.ui.SlotsEnum
import com.alerota.composeanimation.ui.States
import com.alerota.composeanimation.ui.SwipeableNestedScrollConnection
import com.alerota.composeanimation.ui.theme.Spacings
import com.alerota.composeanimation.util.normalize
import kotlin.math.roundToInt

internal val IMAGE_HEIGHT_DP = 290.dp
internal val TOOLBAR_TOP_MARGIN_DP = 7.dp
internal val TOP_CURTAIN_BOTTOM_PADDING_DP = 6.dp
internal const val TOP_CURTAIN_EXTRA_OFFSET_PX = 20f

private const val STICKY_ELEMENT_Z_INDEX = 5f
private const val TOOLBAR_Z_INDEX = 4f
private const val TOP_CURTAIN_Z_INDEX = 3f
private const val BODY_Z_INDEX = 2f

private val toolbarStartElement = Spacings.spaceS

private const val SWIPE_ANIMATION_DURATION_MILLIS = 600


@Suppress("LongMethod")
@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun AnimationWithSubComposeLayout(
    body: @Composable (swipeableState: SwipeableState<States>, scrollState: LazyListState, offset: Int) -> Unit,
) {
    val swipeableState: SwipeableState<States> = rememberSwipeableState(
        initialValue = States.EXPANDED,
        animationSpec = TweenSpec(durationMillis = SWIPE_ANIMATION_DURATION_MILLIS)
    )
    val scrollState = rememberLazyListState()

    val connection = remember {
        SwipeableNestedScrollConnection(
            swipeableState = swipeableState
        )
    }

    val statusBarHeightInPx: Int = WindowInsets.statusBars.getTop(LocalDensity.current)

    SubcomposeLayout { constraints ->
        val expandedOffset = IMAGE_HEIGHT_DP
        val stickyElementHorizontalMargins = Spacings.spaceM

        val startToolbarElementPlaceables = subcompose(SlotsEnum.StartToolbarElement) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Magenta)
            ) {
                Text(
                    modifier = Modifier
                        .padding(18.dp)
                        .background(Color.Magenta),
                    text = "Start",
                    textAlign = TextAlign.Center
                )
            }
        }
            .map { it.measure(constraints) }

        val endToolbarElementPlaceables =
            subcompose(SlotsEnum.EndToolbarElement) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Magenta)
                ) {
                    Text(
                        modifier = Modifier
                            .padding(18.dp)
                            .background(Color.Magenta),
                        text = "End",
                        textAlign = TextAlign.Center
                    )
                }
            }
                .map { it.measure(constraints) }

        val toolbarHeight: Int =
            startToolbarElementPlaceables.plus(endToolbarElementPlaceables)
                .maxByOrNull { it.height }?.height ?: 0

        val collapsedOffsetPx =
            statusBarHeightInPx + TOOLBAR_TOP_MARGIN_DP.roundToPx() + toolbarHeight / 2

        val searchBarPlaceables = subcompose(SlotsEnum.SearchBar) {
            // x start and end of the central element (empty space if there's no element)
            val xCentralElementStart = toolbarStartElement.roundToPx() +
                    (startToolbarElementPlaceables.firstOrNull()?.width ?: 0)
            val xCentralElementEnd = constraints.maxWidth -
                    toolbarStartElement.roundToPx() -
                    (endToolbarElementPlaceables.firstOrNull()?.width ?: 0)

            val yOffset = statusBarHeightInPx +
                    toPx(TOOLBAR_TOP_MARGIN_DP) +
                    toolbarHeight

            SearchBarContainer(
                arguments = StickyElementContainerArguments(
                    swipeableState = swipeableState,
                    horizontalMargins = stickyElementHorizontalMargins,
                    xStart = xCentralElementStart,
                    xEnd = xCentralElementEnd,
                    screenWidth = constraints.maxWidth,
                    dragRange = expandedOffset.toPx() -
                            (statusBarHeightInPx + toPx(TOOLBAR_TOP_MARGIN_DP) + toolbarHeight),
                    yOffset = yOffset,
                ),
                stickyElement = {
                    Box(Modifier
                        .width(300.dp)
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Red)
                    ) {
                        Text(
                            modifier = Modifier.align(Alignment.Center),
                            text = "Search bar",
                            fontSize = 20.sp
                        )
                    }
                },
            )
        }
            .map { it.measure(constraints) }

        val bodyPlaceables = subcompose(SlotsEnum.Body) {
            Box(
                Modifier
                    .swipeable(
                        state = swipeableState,
                        orientation = Orientation.Vertical,
                        anchors = mapOf(
                            collapsedOffsetPx.toFloat() to States.COLLAPSED,
                            expandedOffset.toPx() to States.EXPANDED,
                        )
                    )
                    .nestedScroll(connection)
            ) {
                body(
                    swipeableState,
                    scrollState,
                    swipeableState.offset.value.roundToInt()
                )
            }
        }
            .map { it.measure(constraints) }

        val headerBackgroundPlaceables = subcompose(SlotsEnum.HeaderBackground) {
            Box(
                modifier = Modifier
                    .height(expandedOffset)
                    .fillMaxWidth()
                    .background(Color.Green)
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = "Header Background",
                    fontSize = 20.sp
                )
            }
        }
            .map { it.measure(constraints) }

        layout(constraints.maxWidth, constraints.maxHeight) {
            startToolbarElementPlaceables.forEach {
                it.placeRelative(
                    x = toolbarStartElement.roundToPx(),
                    y = statusBarHeightInPx + TOOLBAR_TOP_MARGIN_DP.roundToPx(),
                    zIndex = TOOLBAR_Z_INDEX
                )
            }

            endToolbarElementPlaceables.forEach {
                it.placeRelative(
                    x = constraints.maxWidth - it.width - toolbarStartElement.roundToPx(),
                    y = statusBarHeightInPx + TOOLBAR_TOP_MARGIN_DP.roundToPx(),
                    zIndex = TOOLBAR_Z_INDEX
                )
            }

            val topCurtainHeightPx = statusBarHeightInPx +
                    TOOLBAR_TOP_MARGIN_DP.roundToPx() +
                    toolbarHeight +
                    TOP_CURTAIN_BOTTOM_PADDING_DP.roundToPx()
            val topCurtainPlaceables = subcompose(SlotsEnum.TopCurtain) {
                // When in collapsed position, the curtain height completely covers the toolbar
                TopCurtain(height = topCurtainHeightPx.toDp())
            }.map { it.measure(constraints) }
            topCurtainPlaceables.forEach {
                it.placeRelative(
                    x = 0,
                    y = -swipeableState.offset.value.normalize(
                        oldMin = collapsedOffsetPx.toFloat(),
                        oldMax = expandedOffset.roundToPx().toFloat(),
                        newMin = 0f,
                        newMax = topCurtainHeightPx.toFloat() + TOP_CURTAIN_EXTRA_OFFSET_PX
                    ).toInt(),
                    zIndex = TOP_CURTAIN_Z_INDEX
                )
            }

            headerBackgroundPlaceables
                .forEach { it.placeRelative(0, 0) }

            searchBarPlaceables.forEach { it.placeRelative(0, 0, STICKY_ELEMENT_Z_INDEX) }

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

@OptIn(ExperimentalMaterialApi::class)
@Preview
@Composable
internal fun AnimationWithSubcomposeLayoutPw() {
    Column(
        Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        AnimationWithSubComposeLayout { _, scrollState, bottomPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Yellow),
                state = scrollState,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(
                    bottom = with(LocalDensity.current) { bottomPadding.toDp() }
                )
            ) {
                item { Spacer(modifier = Modifier.height(60.dp)) }

                items(50) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .padding(horizontal = 20.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Gray)
                    ) {
                        Text(
                            modifier = Modifier.align(Alignment.Center),
                            text = "Item $it",
                            fontSize = 20.sp
                        )
                    }
                }
            }
        }
    }
}
