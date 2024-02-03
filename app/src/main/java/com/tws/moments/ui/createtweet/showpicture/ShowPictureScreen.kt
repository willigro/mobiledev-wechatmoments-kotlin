package com.tws.moments.ui.createtweet.showpicture

import androidx.camera.core.ExperimentalGetImage
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tws.moments.ui.createtweet.shared.ui.CreateTweetNavigationEvent
import com.tws.moments.ui.createtweet.shared.ui.CreateTweetUiState
import com.tws.moments.ui.createtweet.shared.CreateTweetViewModel
import com.tws.moments.ui.createtweet.shared.ui.CreateTweetToolbar

@ExperimentalGetImage
@Composable
fun ShowPictureScreenRoot(
    viewModel: CreateTweetViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    ShowPictureScreen(
        uiState = uiState,
        onNavigationEvent = viewModel::onNavigationEvent,
    )
}

@Composable
fun ShowPictureScreen(
    uiState: CreateTweetUiState,
    onNavigationEvent: (CreateTweetNavigationEvent) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        CreateTweetToolbar(modifier = Modifier) {
            onNavigationEvent(CreateTweetNavigationEvent.TakePicture)
        }

        Text(
            text = "SHOW",
        )

        if (uiState.bitmapExif?.bitmap == null) {
            //
        } else {
            Image(
                bitmap = uiState.bitmapExif.bitmap!!.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}