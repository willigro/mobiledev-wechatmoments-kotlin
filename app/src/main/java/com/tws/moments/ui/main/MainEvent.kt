package com.tws.moments.ui.main

import com.tws.moments.datasource.api.entry.TweetBean

sealed interface MainEvent {
    data object FetchUserBean : MainEvent
    data object FetchTweets : MainEvent
    data object RefreshTweets : MainEvent
    data object FetchMoreTweets : MainEvent
    data class ShareNewComment(
        val tweetBean: TweetBean,
        val comment: String,
    ) : MainEvent
}