package com.tws.moments.repository

import com.tws.moments.api.entry.TweetBean
import com.tws.moments.api.entry.UserBean
import com.tws.moments.api.reqApi
import javax.inject.Inject

interface MomentRepository {
    suspend fun fetchUser(): UserBean
    suspend fun fetchTweets(): List<TweetBean>
}

class MomentRepositoryImpl @Inject constructor(): MomentRepository {
    override suspend fun fetchUser(): UserBean {
        return reqApi.user("jsmith")
    }

    override suspend fun fetchTweets(): List<TweetBean> {
        return reqApi.tweets("jsmith")
    }
}
