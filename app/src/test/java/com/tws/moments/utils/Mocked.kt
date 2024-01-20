package com.tws.moments.utils

import com.tws.moments.datasource.api.entry.TweetBean
import com.tws.moments.datasource.api.entry.UserBean

fun mockTweetBean(
    content: String,
) = TweetBean(
    content = content
)

fun mockUserBean(username: String) = UserBean(
    username = username,
)