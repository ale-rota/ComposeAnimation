package com.alerota.composeanimation.ui

import com.alerota.composeanimation.scaffold.elements.Dimensions
import com.alerota.composeanimation.util.END_ANIMATION_PERCENTAGE
import com.alerota.composeanimation.util.MAX_SCALE
import com.alerota.composeanimation.util.START_ANIMATION_PERCENTAGE
import com.alerota.composeanimation.util.normalize

fun provideDimensions(
    arguments: ShrinkableElementArguments
): Dimensions {
    with(arguments) {
        // x coordinate of the center of the sticky element when the header is collapsed
        val xCenterCollapsed = xStart + ((xEnd - xStart) / 2)

        // space available for the sticky element when the header is expanded
        val expandedWidth =
            (screenWidth - 2 * horizontalMargins).toFloat()
        // space available for the sticky element when the header is collapsed
        val collapsedWidth =
            (xEnd - xStart).toFloat() - 2 * centralElementLateralMargin

        // height of the sticky element when expanded
        val expandedHeight = centralElementExpandedHeightPx
        // height of the sticky element when collapsed
        val collapsedHeight = centralElementCollapsedHeightPx

        // y coordinate where the element starts being shrunk and translated
        val yMax = yOffset + dragRange * START_ANIMATION_PERCENTAGE
        // y coordinate where the element stop being shrunk and translated because it can fit the central empty space
        val yMin = yOffset + dragRange * END_ANIMATION_PERCENTAGE

        val screenCenter = (screenWidth / 2)

        return if (currentOffset < yMax) {
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