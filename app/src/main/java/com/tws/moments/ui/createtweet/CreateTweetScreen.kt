package com.tws.moments.ui.createtweet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.tws.moments.designsystem.components.NavigationWrapper

@Composable
fun CreateTweetScreenRoot(navigationWrapper: NavigationWrapper) {
    CreateTweetScreen(navigationWrapper = navigationWrapper)
}

@Composable
fun CreateTweetScreen(navigationWrapper: NavigationWrapper) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Red)
            .clickable {
                navigationWrapper.pop()
            }
    )
}