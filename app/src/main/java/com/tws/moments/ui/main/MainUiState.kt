package com.tws.moments.ui.main

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.tws.moments.datasource.shared.data.TweetBean
import com.tws.moments.datasource.api.entry.UserBean

data class MainUiState(
    val isRefreshing: Boolean = false,
    val isFetchingMore: Boolean = false,
    val userBean: UserBean? = null,
    val tweets: SnapshotStateList<TweetBean>? = null,
)