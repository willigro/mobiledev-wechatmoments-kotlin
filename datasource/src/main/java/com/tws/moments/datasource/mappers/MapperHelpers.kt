package com.tws.moments.datasource.mappers

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import com.tws.moments.datasource.api.entry.TweetBeanApi
import com.tws.moments.datasource.shared.data.TweetBean
import com.tws.moments.datasource.utils.DateUtils

fun List<TweetBeanApi>?.mapToTweetBean(
    dateUtils: DateUtils,
) = this?.map {
    TweetBean(
        content = it.content,
        sender = it.sender,
        images = it.images,
        comments = it.comments?.toMutableStateList() ?: mutableStateListOf(),
        error = it.error,
        unknownError = it.unknownError,
        time = dateUtils.formatDateFromServerToTimeLapsed(it.time),
    )
}