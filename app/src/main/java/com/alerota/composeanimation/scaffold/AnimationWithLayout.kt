package com.alerota.composeanimation.scaffold

import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alerota.composeanimation.scaffold.elements.BackgroundImage
import com.alerota.composeanimation.scaffold.elements.HeaderButton
import com.alerota.composeanimation.scaffold.elements.SearchBar
import com.alerota.composeanimation.scaffold.elements.TopCurtain
import com.alerota.composeanimation.scaffold.elements.toPx
import com.alerota.composeanimation.ui.ShrinkableElementArguments
import com.alerota.composeanimation.ui.SlotsEnum
import com.alerota.composeanimation.ui.States
import com.alerota.composeanimation.ui.SwipeableNestedScrollConnection
import com.alerota.composeanimation.ui.provideDimensions
import com.alerota.composeanimation.ui.theme.Spacings
import com.alerota.composeanimation.util.CENTRAL_ELEMENT_COLLAPSED_HEIGHT_DP
import com.alerota.composeanimation.util.CENTRAL_ELEMENT_EXPANDED_HEIGHT_DP
import com.alerota.composeanimation.util.centralElementLateralMargin
import com.alerota.composeanimation.util.normalize
import kotlin.math.roundToInt

private const val STICKY_ELEMENT_Z_INDEX = 5f
private const val TOOLBAR_Z_INDEX = 4f
private const val TOP_CURTAIN_Z_INDEX = 3f
private const val BODY_Z_INDEX = 2f

private val toolbarElementMargin = Spacings.spaceS

private const val SWIPE_ANIMATION_DURATION_MILLIS = 600


@Suppress("LongMethod")
@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun AnimationWithLayout(
    body: @Composable (scrollState: LazyListState) -> Unit,
) {
    val density = LocalDensity.current
    val anchoredDraggableState: AnchoredDraggableState<States> = remember {
        AnchoredDraggableState(
            initialValue = States.EXPANDED,
            anchors = DraggableAnchors { },
            positionalThreshold = { distance: Float -> distance * 0.5f },
            velocityThreshold = { with(density) { 100.dp.toPx() } },
            animationSpec = TweenSpec(durationMillis = SWIPE_ANIMATION_DURATION_MILLIS)
        )
    }
    val scrollState = rememberLazyListState()

    val connection = remember {
        SwipeableNestedScrollConnection(
            anchoredDraggableState = anchoredDraggableState
        )
    }

    val statusBarHeightInPx: Int = WindowInsets.statusBars.getTop(LocalDensity.current)

    val expandedWidth = with(LocalDensity.current) {
        LocalConfiguration.current.screenWidthDp.dp.toPx() - Spacings.spaceM.toPx()
    }.toFloat()

    val expandedHeight = toPx(CENTRAL_ELEMENT_EXPANDED_HEIGHT_DP).toFloat()
    val toolbarElementMarginPx = with(LocalDensity.current) { toolbarElementMargin.toPx() }
    val toolbarTopMarginPx = toPx(TOOLBAR_TOP_MARGIN_DP)
    val stickyElementHorizontalMarginsPx = toPx(Spacings.spaceM)
    val screenWidth =
        with(LocalDensity.current) { LocalConfiguration.current.screenWidthDp.dp.toPx() }
    val expandedOffset = IMAGE_HEIGHT_DP
    val expandedOffsetPx = with(LocalDensity.current) { expandedOffset.toPx() }
    val centralElementExpandedHeightPx = toPx(CENTRAL_ELEMENT_EXPANDED_HEIGHT_DP).toFloat()
    val centralElementCollapsedHeightPx = toPx(CENTRAL_ELEMENT_COLLAPSED_HEIGHT_DP).toFloat()
    val centralElementLateralMarginPx = toPx(centralElementLateralMargin)

    Layout(
        content = {

            HeaderButton(
                modifier = Modifier
                    .layoutId(SlotsEnum.StartToolbarElement),
                text = "Start"
            )

            HeaderButton(
                modifier = Modifier
                    .layoutId(SlotsEnum.EndToolbarElement),
                text = "End"
            )

            SearchBar(modifier = Modifier.layoutId(SlotsEnum.SearchBar))

            Box(
                Modifier
                    .layoutId(SlotsEnum.Body)
                    .anchoredDraggable(
                        state = anchoredDraggableState,
                        orientation = Orientation.Vertical
                    )
                    .nestedScroll(connection)
            ) {
                body(scrollState)
            }

            BackgroundImage(
                modifier = Modifier
                    .layoutId(SlotsEnum.HeaderBackground)
                    .height(expandedOffset)
                    .fillMaxWidth()
            )

            TopCurtain(modifier = Modifier.layoutId(SlotsEnum.TopCurtain))

        }
    ) { measurables, constraints ->

        val startToolbarElementPlaceables = measurables
            .first { it.layoutId == SlotsEnum.StartToolbarElement }.measure(constraints)

        val endToolbarElementPlaceables = measurables
            .first { it.layoutId == SlotsEnum.EndToolbarElement }.measure(constraints)


        // Toolbar is as tall as the tallest element
        val toolbarHeight: Int =
            listOf(startToolbarElementPlaceables, endToolbarElementPlaceables)
                .maxByOrNull { it.height }?.height ?: 0

        val collapsedOffsetPx =
            statusBarHeightInPx + TOOLBAR_TOP_MARGIN_DP.roundToPx() + toolbarHeight / 2

        anchoredDraggableState.updateAnchors(
            DraggableAnchors {
                States.COLLAPSED at collapsedOffsetPx.toFloat()
                States.EXPANDED at expandedOffsetPx
            }
        )

        // x start and end of the central element (empty space if there's no element)
        val xCentralElementStart =
            toolbarElementMargin.roundToPx() + startToolbarElementPlaceables.width
        val xCentralElementEnd =
            constraints.maxWidth - toolbarElementMarginPx - endToolbarElementPlaceables.width

        val yOffset = statusBarHeightInPx + toolbarTopMarginPx + toolbarHeight

        val dimensions = provideDimensions(
            arguments = ShrinkableElementArguments(
                anchoredDraggableState = anchoredDraggableState,
                horizontalMargins = stickyElementHorizontalMarginsPx,
                xStart = xCentralElementStart,
                xEnd = xCentralElementEnd.toInt(),
                screenWidth = screenWidth.toInt(),
                dragRange = expandedOffsetPx - (statusBarHeightInPx + toolbarTopMarginPx + toolbarHeight),
                yOffset = yOffset,
                centralElementExpandedHeightPx = centralElementExpandedHeightPx,
                centralElementCollapsedHeightPx = centralElementCollapsedHeightPx,
                centralElementLateralMargin = centralElementLateralMarginPx,
                currentOffset = anchoredDraggableState.offset
            )
        )


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

        layout(constraints.maxWidth, constraints.maxHeight) {
            startToolbarElementPlaceables.placeRelative(
                x = toolbarElementMargin.roundToPx(),
                y = statusBarHeightInPx + TOOLBAR_TOP_MARGIN_DP.roundToPx(),
                zIndex = TOOLBAR_Z_INDEX
            )


            endToolbarElementPlaceables.placeRelative(
                x = constraints.maxWidth - endToolbarElementPlaceables.width - toolbarElementMargin.roundToPx(),
                y = statusBarHeightInPx + TOOLBAR_TOP_MARGIN_DP.roundToPx(),
                zIndex = TOOLBAR_Z_INDEX
            )

            topCurtainPlaceables.placeRelative(
                x = 0,
                y = -anchoredDraggableState.offset.normalize(
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
                y = anchoredDraggableState.offset.roundToInt() - searchBarPlaceables.height / 2,
                zIndex = STICKY_ELEMENT_Z_INDEX
            )

            bodyPlaceables.placeRelative(
                x = 0,
                y = anchoredDraggableState.offset.roundToInt(),
                zIndex = BODY_Z_INDEX
            )

        }
    }

}

@Preview
@Composable
internal fun AnimationWithLayoutPw() {
    Column(
        Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        AnimationWithLayout { scrollState ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Yellow),
                state = scrollState,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(
                    bottom = with(LocalDensity.current) { 250.toDp() }
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
