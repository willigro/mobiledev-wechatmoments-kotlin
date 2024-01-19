package com.tws.moments.datasource.repository

import com.tws.moments.datasource.api.MomentService
import com.tws.moments.datasource.api.entry.TweetBean
import com.tws.moments.datasource.api.entry.UserBean
import javax.inject.Inject

interface MomentRepository {
    suspend fun fetchUser(): UserBean
    suspend fun fetchTweets(): List<TweetBean>
}

class MomentRepositoryImpl @Inject constructor(
    private val momentService: MomentService,
): MomentRepository {
    override suspend fun fetchUser(): UserBean {
        return momentService.user("jsmith")
    }

    override suspend fun fetchTweets(): List<TweetBean> {
        return momentService.tweets("jsmith")
    }
}
