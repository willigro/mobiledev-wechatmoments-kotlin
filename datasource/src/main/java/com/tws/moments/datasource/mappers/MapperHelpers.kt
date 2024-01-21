package com.tws.moments.datasource.mappers

import androidx.compose.runtime.toMutableStateList
import com.tws.moments.datasource.api.entry.TweetBeanServer
import com.tws.moments.datasource.shared.data.TweetBean

fun List<TweetBeanServer>?.mapToTweetBean() = this?.map {
    TweetBean(
        content = it.content,
        sender = it.sender,
        images = it.images,
        comments = it.comments?.toMutableStateList(),
        error = it.error,
        unknownError = it.unknownError,
    )
}