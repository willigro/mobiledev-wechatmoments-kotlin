package com.tws.moments.ui.createtweet.save

import androidx.camera.core.ExperimentalGetImage
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.tws.moments.ui.createtweet.shared.ui.CreateTweetNavigationEvent
import com.tws.moments.ui.createtweet.shared.CreateTweetViewModel
import com.tws.moments.ui.createtweet.shared.ui.CreateTweetToolbar

@ExperimentalGetImage
@Composable
fun SaveTweetScreenRoot(
    viewModel: CreateTweetViewModel = hiltViewModel(),
) {
    SaveTweetScreen(
        onNavigationEvent = viewModel::onNavigationEvent,
    )
}

@Composable
fun SaveTweetScreen(
    onNavigationEvent: (CreateTweetNavigationEvent) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        CreateTweetToolbar(modifier = Modifier) {
            onNavigationEvent(CreateTweetNavigationEvent.Back)
        }

        Text(
            text = "SAVE",
        )
    }
}