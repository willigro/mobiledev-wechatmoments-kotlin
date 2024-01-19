package com.tws.moments.datasource.test

import com.tws.moments.datasource.api.entry.TweetBean

// TODO (rittmann) better handle it to do not compile it when its not testing
fun mockTweetBean(
    content: String,
) = TweetBean(
    content = content
)