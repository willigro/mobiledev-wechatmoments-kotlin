package com.tws.moments.ui.createtweet.single

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.tws.moments.designsystem.components.NavigationWrapper
import com.tws.moments.ui.createtweet.shared.CreateTweetViewModel
import com.tws.moments.ui.createtweet.shared.ui.CreateTweetToolbar

@Composable
fun NewTweetSinglePictureScreenRoot(
    navigationWrapper: NavigationWrapper,
    viewModel: CreateTweetViewModel = hiltViewModel(),
) {
    NewTweetSinglePictureScreen(navigationWrapper = navigationWrapper)
}

@Composable
fun NewTweetSinglePictureScreen(navigationWrapper: NavigationWrapper) {
    Column(modifier = Modifier.fillMaxSize()) {
        CreateTweetToolbar(modifier = Modifier, navigationWrapper = navigationWrapper)
    }
}