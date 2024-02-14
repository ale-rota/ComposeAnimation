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
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alerota.composeanimation.ui.SlotsEnum
import com.alerota.composeanimation.ui.States
import com.alerota.composeanimation.ui.SwipeableNestedScrollConnection
import com.alerota.composeanimation.ui.theme.Spacings
import com.alerota.composeanimation.util.CENTRAL_ELEMENT_COLLAPSED_HEIGHT_DP
import com.alerota.composeanimation.util.CENTRAL_ELEMENT_EXPANDED_HEIGHT_DP
import com.alerota.composeanimation.util.centralElementLateralMargin
import com.alerota.composeanimation.util.normalize
import com.alerota.composeanimation.util.provideDimensions
import kotlin.math.roundToInt

private const val STICKY_ELEMENT_Z_INDEX = 5f
private const val TOOLBAR_Z_INDEX = 4f
private const val TOP_CURTAIN_Z_INDEX = 3f
private const val BODY_Z_INDEX = 2f

private val toolbarStartElement = Spacings.spaceS

private const val SWIPE_ANIMATION_DURATION_MILLIS = 600


@Suppress("LongMethod")
@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun AnimationWithLayout(
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

    val expandedWidth = with(LocalDensity.current) {
        LocalConfiguration.current.screenWidthDp.dp.toPx() - Spacings.spaceM.toPx()
    }.toFloat()

    val expandedHeight = toPx(CENTRAL_ELEMENT_EXPANDED_HEIGHT_DP).toFloat()
    val toolbarStartElementPx = with(LocalDensity.current) { toolbarStartElement.toPx() }
    val toolbarTopMarginPx = toPx(TOOLBAR_TOP_MARGIN_DP)
    val stickyElementHorizontalMarginsPx = toPx(Spacings.spaceM)
    val screenWidth = with(LocalDensity.current) {LocalConfiguration.current.screenWidthDp.dp.toPx() }
    val expandedOffset = IMAGE_HEIGHT_DP
    val expandedOffsetPx = with(LocalDensity.current) { expandedOffset.toPx() }
    val centralElementExpandedHeightPx = toPx(CENTRAL_ELEMENT_EXPANDED_HEIGHT_DP).toFloat()
    val centralElementCollapsedHeightPx = toPx(CENTRAL_ELEMENT_COLLAPSED_HEIGHT_DP).toFloat()
    val centralElementLateralMarginPx = toPx(centralElementLateralMargin)
    val collapsedOffsetPx = statusBarHeightInPx + LocalDensity.current.run { TOOLBAR_TOP_MARGIN_DP.toPx() }

    Layout(
        content = {
            Box(
                modifier = Modifier
                    .layoutId(SlotsEnum.StartToolbarElement)
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

            Box(
                modifier = Modifier
                    .layoutId(SlotsEnum.EndToolbarElement)
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


            Box(Modifier
                .layoutId(SlotsEnum.SearchBar)
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

            Box(
                Modifier
                    .layoutId(SlotsEnum.Body)
                    .swipeable(
                        state = swipeableState,
                        orientation = Orientation.Vertical,
                        anchors = mapOf(
                            collapsedOffsetPx to States.COLLAPSED,
                            expandedOffsetPx to States.EXPANDED,
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

            Box(
                modifier = Modifier
                    .layoutId(SlotsEnum.HeaderBackground)
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

            TopCurtain(
                modifier = Modifier.layoutId(SlotsEnum.TopCurtain),
                height = 0.dp
            )

        }
    ) { measurables, constraints ->

        val startToolbarElementPlaceables = measurables
            .first { it.layoutId == SlotsEnum.StartToolbarElement }.measure(constraints)

        val endToolbarElementPlaceables = measurables
            .first { it.layoutId == SlotsEnum.EndToolbarElement }.measure(constraints)


        val toolbarHeight: Int =
            listOf(startToolbarElementPlaceables, endToolbarElementPlaceables)
                .maxByOrNull { it.height }?.height ?: 0

        val collapsedOffsetPx =
            statusBarHeightInPx + TOOLBAR_TOP_MARGIN_DP.roundToPx() + toolbarHeight / 2

        // x start and end of the central element (empty space if there's no element)
        val xCentralElementStart =
            toolbarStartElement.roundToPx() + startToolbarElementPlaceables.width
        val xCentralElementEnd =
            constraints.maxWidth - toolbarStartElementPx - endToolbarElementPlaceables.width

        val yOffset = statusBarHeightInPx + toolbarTopMarginPx + toolbarHeight

        val dimensions = provideDimensions(
            arguments = StickyElementContainerArgumentsV2(
                swipeableState = swipeableState,
                horizontalMargins = stickyElementHorizontalMarginsPx,
                xStart = xCentralElementStart,
                xEnd = xCentralElementEnd.toInt(),
                screenWidth = screenWidth.toInt(),
                dragRange = expandedOffsetPx - (statusBarHeightInPx + toolbarTopMarginPx + toolbarHeight),
                yOffset = yOffset,
                centralElementExpandedHeightPx = centralElementExpandedHeightPx,
                centralElementCollapsedHeightPx = centralElementCollapsedHeightPx,
                centralElementLateralMargin = centralElementLateralMarginPx,
                currentOffset = swipeableState.offset.value
            )
        )


        println("alerota h=${dimensions.horizontalScale} v=${dimensions.verticalScale}")
        val searchBarPlaceables = measurables
            .first { it.layoutId == SlotsEnum.SearchBar }.measure(
                constraints.copy(
                    minWidth = (expandedWidth * dimensions.horizontalScale).toInt(),
                    maxWidth = (expandedWidth * dimensions.horizontalScale).toInt(),
                    minHeight = (expandedHeight * dimensions.verticalScale).toInt(),
                    maxHeight = (expandedHeight * dimensions.verticalScale).toInt()
                )
            )

        val bodyPlaceables = measurables
            .first { it.layoutId == SlotsEnum.Body }.measure(constraints)

        val headerBackgroundPlaceables = measurables
            .first { it.layoutId == SlotsEnum.HeaderBackground }.measure(constraints)

        val topCurtainHeightPx = statusBarHeightInPx +
                TOOLBAR_TOP_MARGIN_DP.roundToPx() +
                toolbarHeight +
                TOP_CURTAIN_BOTTOM_PADDING_DP.roundToPx()
        val topCurtainPlaceables = measurables
            .first { it.layoutId == SlotsEnum.TopCurtain }.measure(
                constraints.copy(
                    minHeight = topCurtainHeightPx,
                    maxHeight = topCurtainHeightPx
                )
            )

        layout(0, 0) {
            startToolbarElementPlaceables.placeRelative(
                x = toolbarStartElement.roundToPx(),
                y = statusBarHeightInPx + TOOLBAR_TOP_MARGIN_DP.roundToPx(),
                zIndex = TOOLBAR_Z_INDEX
            )


            endToolbarElementPlaceables.placeRelative(
                x = constraints.maxWidth - endToolbarElementPlaceables.width - toolbarStartElement.roundToPx(),
                y = statusBarHeightInPx + TOOLBAR_TOP_MARGIN_DP.roundToPx(),
                zIndex = TOOLBAR_Z_INDEX
            )

            topCurtainPlaceables.placeRelative(
                x = 0,
                y = -swipeableState.offset.value.normalize(
                    oldMin = collapsedOffsetPx.toFloat(),
                    oldMax = expandedOffset.roundToPx().toFloat(),
                    newMin = 0f,
                    newMax = topCurtainHeightPx.toFloat() + TOP_CURTAIN_EXTRA_OFFSET_PX
                ).toInt(),
                zIndex = TOP_CURTAIN_Z_INDEX
            )


            headerBackgroundPlaceables.placeRelative(0, 0)

            searchBarPlaceables.placeRelative(
                x = dimensions.xCenter - searchBarPlaceables.width / 2,
                y = swipeableState.offset.value.roundToInt() - searchBarPlaceables.height / 2,
                zIndex = STICKY_ELEMENT_Z_INDEX
            )

            bodyPlaceables.placeRelative(
                x = 0,
                y = swipeableState.offset.value.roundToInt(),
                zIndex = BODY_Z_INDEX
            )

        }
    }

}

@OptIn(ExperimentalMaterialApi::class)
@Preview
@Composable
internal fun AnimationWithLayoutPw() {
    Column(
        Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        AnimationWithLayout { _, scrollState, bottomPadding ->
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
