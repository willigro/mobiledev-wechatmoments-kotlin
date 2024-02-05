package com.tws.moments.ui.createtweet.shared.ui

sealed interface CreateTweetNavigationEvent {
    data object Back : CreateTweetNavigationEvent
    data object TakePicture : CreateTweetNavigationEvent
    data object ShowPicture : CreateTweetNavigationEvent
    data object Closes : CreateTweetNavigationEvent
}