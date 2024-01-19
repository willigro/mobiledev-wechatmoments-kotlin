package com.tws.moments.ui.main

import com.tws.moments.api.entry.TweetBean

sealed interface MainEvent {
    data object FetchUserBean : MainEvent
    data object FetchTweets : MainEvent
    data object FetchMoreTweets : MainEvent
    data class ShareNewComment(
        val tweetBean: TweetBean,
    ) : MainEvent
}