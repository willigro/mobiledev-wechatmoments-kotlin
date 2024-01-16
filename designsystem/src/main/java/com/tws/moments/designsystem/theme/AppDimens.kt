package com.tws.moments.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * TODO: move dimens to resources
 * */
@Immutable
data class AppDimensions(
    val zero: Dp = 0.dp,

    val paddingScreenDefault: Dp = 24.dp,

    val paddingSmall: Dp = 4.dp,
    val paddingMedium: Dp = 12.dp,
    val paddingLarge: Dp = 24.dp,

    val paddingSpaceBetweenComponentsSmall: Dp = 6.dp,
    val paddingSpaceBetweenComponentsSmallX: Dp = 14.dp,
    val paddingSpaceBetweenComponentsMedium: Dp = 18.dp,
    val paddingSpaceBetweenComponentsMediumX: Dp = 22.dp,
    val paddingSpaceBetweenComponentsLarge: Dp = 26.dp,

    val dividerThickness: Dp = 2.dp,

    val borderThicknessSmall: Dp = 1.dp,
)

internal val LocalDimensions = staticCompositionLocalOf { AppDimensions() }