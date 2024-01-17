package com.tws.moments.viewmodels

sealed interface MainEvent {
    data object FetchUserBean: MainEvent
    data object FetchTweets: MainEvent
    data object FetchMoreTweets: MainEvent
}