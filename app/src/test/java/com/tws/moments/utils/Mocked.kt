package com.tws.moments.utils

import com.tws.moments.datasource.api.entry.TweetBean

fun mockTweetBean(
    content: String,
) = TweetBean(
    content = content
)