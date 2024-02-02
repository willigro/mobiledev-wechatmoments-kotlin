package com.tws.moments.ui.createtweet.save

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.tws.moments.designsystem.components.NavigationWrapper
import com.tws.moments.ui.createtweet.shared.CreateTweetViewModel
import com.tws.moments.ui.createtweet.shared.ui.CreateTweetToolbar

@Composable
fun SaveTweetScreenRoot(
    navigationWrapper: NavigationWrapper,
    viewModel: CreateTweetViewModel = hiltViewModel(),
) {
    SaveTweetScreen(navigationWrapper = navigationWrapper)
}

@Composable
fun SaveTweetScreen(navigationWrapper: NavigationWrapper) {
    Column(modifier = Modifier.fillMaxSize()) {
        CreateTweetToolbar(modifier = Modifier, navigationWrapper = navigationWrapper)
    }
}