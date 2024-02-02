package com.tws.moments.ui.createtweet.take.single

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.tws.moments.designsystem.components.NavigationWrapper
import com.tws.moments.ui.createtweet.shared.CreateTweetViewModel
import com.tws.moments.ui.createtweet.shared.ui.CreateTweetToolbar
import com.tws.moments.ui.navigation.ScreensNavigation

/**
 *
 * Steps Single picture:
 *     - Take/Select picture
 *     - Add filter (for version 2)
 *     - Add content
 *         - Save
 *
 * */

@Composable
fun TakeSinglePictureScreenRoot(
    navigationWrapper: NavigationWrapper,
    viewModel: CreateTweetViewModel = hiltViewModel(),
) {
    TakeSinglePictureScreen(navigationWrapper = navigationWrapper)
}

@Composable
fun TakeSinglePictureScreen(navigationWrapper: NavigationWrapper) {
    Column(modifier = Modifier.fillMaxSize()) {
        CreateTweetToolbar(modifier = Modifier, navigationWrapper = navigationWrapper)
        Text(
            text = "Next",
            modifier = Modifier.clickable { navigationWrapper.navigate(ScreensNavigation.CreateTweet.SaveTweet.destination) }
        )
    }
}