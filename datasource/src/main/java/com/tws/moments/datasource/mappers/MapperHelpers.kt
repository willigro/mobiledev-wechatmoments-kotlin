package com.tws.moments.datasource.mappers

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import com.tws.moments.datasource.api.entry.TweetBeanApi
import com.tws.moments.datasource.shared.data.TweetBean
import com.tws.moments.datasource.utils.DateUtils

fun List<TweetBeanApi>?.mapToTweetBean(
    dateUtils: DateUtils,
) = this?.map { tweet ->
    TweetBean(
        content = tweet.content,
        sender = tweet.sender,
        imagesUrls = tweet.images?.asSequence()
            ?.map { it.url ?: "" }
            ?.filter { it.isNotEmpty() }
            ?.toList(),
        comments = tweet.comments?.toMutableStateList() ?: mutableStateListOf(),
        error = tweet.error,
        unknownError = tweet.unknownError,
        time = dateUtils.formatDateFromServerToTimeLapsed(tweet.time),
    )
}