package com.tws.moments.ui.createtweet.shared.ui

import androidx.camera.core.ImageCapture

sealed interface CreateTweetUiEvent {
    data class SavePicture(
        val content: String
    ) : CreateTweetUiEvent

    data class TakePicture(val imageCapture: ImageCapture) : CreateTweetUiEvent
}