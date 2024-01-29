package com.tws.moments.ui.main

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.tws.moments.datasource.api.entry.UserBean
import com.tws.moments.datasource.shared.data.TweetBean

data class MainUiState(
    val isRefreshing: Boolean = false,
    val isFetchingMore: Boolean = false,
    val isSendingComment: Boolean = false,
    val isLoading: Boolean = true,
    val hasErrorOnTweets: Boolean = false,
    val userBean: UserBean? = null,
    val tweets: SnapshotStateList<TweetBean>? = null,
)