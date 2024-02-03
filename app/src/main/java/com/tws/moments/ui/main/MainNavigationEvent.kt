package com.tws.moments.ui.main

sealed interface MainNavigationEvent {
    data object OpenCreateTweet : MainNavigationEvent
}