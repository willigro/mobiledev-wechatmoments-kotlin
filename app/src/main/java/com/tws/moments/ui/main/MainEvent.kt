package com.tws.moments.ui.main

import com.tws.moments.datasource.shared.data.TweetBean

sealed interface MainEvent {
    data object FetchUserBean : MainEvent
    data object FetchTweets : MainEvent
    data object RefreshTweets : MainEvent
    data object FetchMoreTweets : MainEvent
    data class ShareNewComment(
        val tweetBean: TweetBean,
        val comment: String,
    ) : MainEvent

    data class OpenImage(val url: List<String>?) : MainEvent
    data object ClosesImage : MainEvent
}