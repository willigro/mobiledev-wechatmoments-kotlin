package com.tws.moments.ui.main

sealed interface MainEvent {
    data object FetchUserBean: MainEvent
    data object FetchTweets: MainEvent
    data object FetchMoreTweets: MainEvent
}