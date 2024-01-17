package com.tws.moments.viewmodels

import com.tws.moments.api.entry.TweetBean
import com.tws.moments.api.entry.UserBean

data class MainUiState(
    val isRefreshing: Boolean = false,
    val isFetchingMore: Boolean = false,
    val userBean: UserBean? = null,
    val tweets: List<TweetBean>? = null,
    val allTweets: List<TweetBean>? = null,
)