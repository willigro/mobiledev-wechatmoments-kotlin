package com.tws.moments.datasource.mappers

import androidx.compose.runtime.toMutableStateList
import com.tws.moments.datasource.api.entry.TweetBeanApi
import com.tws.moments.datasource.shared.data.TweetBean

fun List<TweetBeanApi>?.mapToTweetBean() = this?.map {
    TweetBean(
        content = it.content,
        sender = it.sender,
        images = it.images,
        comments = it.comments?.toMutableStateList(),
        error = it.error,
        unknownError = it.unknownError,
    )
}