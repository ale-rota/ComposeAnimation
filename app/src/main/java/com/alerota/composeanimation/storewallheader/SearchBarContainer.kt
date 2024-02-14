@file:OptIn(ExperimentalMaterialApi::class)

package com.alerota.composeanimation.storewallheader

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.alerota.composeanimation.ui.States
import com.alerota.composeanimation.util.CENTRAL_ELEMENT_COLLAPSED_HEIGHT_DP
import com.alerota.composeanimation.util.CENTRAL_ELEMENT_EXPANDED_HEIGHT_DP
import com.alerota.composeanimation.util.END_ANIMATION_PERCENTAGE
import com.alerota.composeanimation.util.MAX_SCALE
import com.alerota.composeanimation.util.START_ANIMATION_PERCENTAGE
import com.alerota.composeanimation.util.centralElementLateralMargin
import com.alerota.composeanimation.util.normalize
import kotlin.math.roundToInt



/**
 * Container for the sticky element of the [AnimationWithSubComposeLayout].
 * When the [swipeableState] value changes - because the user is collapsing/expanding the header, an animation
 * takes place, which does the following:
 * - it moves the sticky element up
 * - it translates horizontally and shrinks the sticky element so it fits the empty space between the lateral
 * elements of the toolbar (e.g. back button and toggle).*
 *
 * @param swipeableState state of the swipeable. It contains information about the current offset of the animation
 * @param xStart start x coordinate (relative to the screen) of the space available for the collapsed sticky element
 * @param xEnd end x coordinate (relative to the screen) of the space available for the collapsed sticky element
 * @param screenWidth screen width in px
 * @param dragRange y-axis interval where the animation takes place
 * @param yOffset y-axis animation offset
 * @param stickyElement sticky element composable
 *
 */
@OptIn(ExperimentalMaterialApi::class)
@Suppress("LongMethod")
@Composable
fun SearchBarContainer(
    arguments: StickyElementContainerArguments,
    stickyElement: @Composable () -> Unit,
) {
    with(arguments) {
        // x coordinate of the center of the sticky element when the header is collapsed
        val xCenterCollapsed = xStart + ((xEnd - xStart) / 2)

        // space available for the sticky element when the header is expanded
        val expandedWidth =
            (screenWidth - 2 * with(LocalDensity.current) { horizontalMargins.roundToPx() }).toFloat()
        // space available for the sticky element when the header is collapsed
        val collapsedWidth =
            (xEnd - xStart).toFloat() - 2 * toPx(centralElementLateralMargin)

        // height of the sticky element when expanded
        val expandedHeight = toPx(CENTRAL_ELEMENT_EXPANDED_HEIGHT_DP).toFloat()
        // height of the sticky element when collapsed
        val collapsedHeight = toPx(CENTRAL_ELEMENT_COLLAPSED_HEIGHT_DP).toFloat()

        // y coordinate where the element starts being shrunk and translated
        val yMax = yOffset + dragRange * START_ANIMATION_PERCENTAGE
        // y coordinate where the element stop being shrunk and translated because it can fit the central empty space
        val yMin = yOffset + dragRange * END_ANIMATION_PERCENTAGE

        val currentOffset = swipeableState.offset.value
        val screenCenter = (screenWidth / 2)

        val dimensions: State<Dimensions> = remember(collapsedWidth, currentOffset) {
            derivedStateOf {
                if (currentOffset < yMax) {
                    val xCenter = currentOffset.normalize(
                        oldMin = yMin,
                        oldMax = yMax,
                        newMin = xCenterCollapsed.toFloat(),
                        newMax = screenCenter.toFloat()
                    ).toInt()
                    val newWidth = currentOffset.normalize(
                        oldMin = yMin,
                        oldMax = yMax,
                        newMin = collapsedWidth,
                        newMax = expandedWidth
                    )
                    val newHeight: Int = currentOffset.normalize(
                        oldMin = yMin,
                        oldMax = yMax,
                        newMin = collapsedHeight,
                        newMax = expandedHeight
                    ).toInt()

                    Dimensions(
                        xCenter = xCenter,
                        horizontalScale = newWidth / expandedWidth,
                        verticalScale = newHeight / expandedHeight
                    )
                } else Dimensions(
                    xCenter = screenCenter,
                    horizontalScale = MAX_SCALE,
                    verticalScale = MAX_SCALE
                )
            }

        }

        println("alerota h=${dimensions.value.horizontalScale} v=${dimensions.value.verticalScale}")

        Box(
            modifier = Modifier
                .width(with(LocalDensity.current) { expandedWidth.toDp() } * dimensions.value.horizontalScale)
                .height(with(LocalDensity.current) { expandedHeight.toDp() } * dimensions.value.verticalScale)
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)

                    layout(placeable.width, placeable.height) {
                        placeable.place(
                            x = dimensions.value.xCenter - placeable.width / 2,
                            y = swipeableState.offset.value.roundToInt() - placeable.height / 2
                        )
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            stickyElement()
        }
    }
}

@Composable
fun toPx(dp: Dp) = with(LocalDensity.current) { dp.roundToPx() }

class StickyElementContainerArguments(
    val swipeableState: SwipeableState<States>,
    val horizontalMargins: Dp,
    val xStart: Int,
    val xEnd: Int,
    val screenWidth: Int,
    val dragRange: Float,
    val yOffset: Int,
)

/**
 * @param xCenter x-axis coordinate of the center of the element
 * @param horizontalScale horizontal scale of the element
 * @param verticalScale vertical scale of the element
 */
data class Dimensions(
    val xCenter: Int,
    val horizontalScale: Float,
    val verticalScale: Float
)
