package com.tws.moments.ui.createtweet.single

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.tws.moments.designsystem.components.NavigationWrapper
import com.tws.moments.ui.createtweet.CreateTweetViewModel

@Composable
fun NewTweetSinglePictureScreenRoot(
    navigationWrapper: NavigationWrapper,
    viewModel: CreateTweetViewModel = hiltViewModel(),
) {
    NewTweetSinglePictureScreen()
}

@Composable
fun NewTweetSinglePictureScreen() {

}