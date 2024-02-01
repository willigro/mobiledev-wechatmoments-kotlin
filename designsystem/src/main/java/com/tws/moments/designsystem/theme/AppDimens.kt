package com.tws.moments.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class AppDimensions(
    val zero: Dp = 0.dp,

    val paddingScreenDefault: Dp = 24.dp,

    val paddingSmall: Dp = 4.dp,
    val paddingMedium: Dp = 12.dp,
    val paddingLarge: Dp = 24.dp,

    val paddingSpaceBetweenComponentsSmall: Dp = 5.dp,
    val paddingSpaceBetweenComponentsSmallX: Dp = 10.dp,
    val paddingSpaceBetweenComponentsMedium: Dp = 16.dp,
    val paddingSpaceBetweenComponentsMediumX: Dp = 20.dp,
    val paddingSpaceBetweenComponentsLarge: Dp = 26.dp,

    val paddingToolbar: Dp = 8.dp,

    val dividerThickness: Dp = 1.dp,

    val progressSizeSmall: Dp = 20.dp,

    val baseTweet: BaseTweet = BaseTweet(),
)

data class BaseTweet(
    val avatarSize: Dp = 40.dp,
    val userProfileMinHeight: Dp = 200.dp,
    val userAvatarSize: Dp = 70.dp,
    val paddingTopSendNickname: Dp = 1.dp,
    val paddingBottomComment: Dp = 5.dp,
    val paddingBottomUserNickname: Dp = 25.dp ,
    val singleImageWidth: Dp = 100.dp,
    val singleImageHeight: Dp = 150.dp,
    val gridImageSize: Dp = 94.dp,
)

internal val LocalDimensions = staticCompositionLocalOf { AppDimensions() }