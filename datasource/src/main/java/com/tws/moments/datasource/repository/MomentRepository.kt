package com.tws.moments.datasource.repository

import com.tws.moments.datasource.api.MomentService
import com.tws.moments.datasource.api.entry.TweetBean
import com.tws.moments.datasource.api.entry.UserBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface MomentRepository {
    suspend fun fetchUser(): UserBean
    suspend fun fetchTweets(): List<TweetBean>
}

class MomentRepositoryImpl @Inject constructor(
    private val momentService: MomentService,
    // TODO (rittmann) add a cache, can be a DAO, Preferences, File, anything
) : MomentRepository {
    override suspend fun fetchUser(): UserBean = withContext(Dispatchers.IO) {
        momentService.user("jsmith")
    }

    override suspend fun fetchTweets(): List<TweetBean> = withContext(Dispatchers.IO) {
        momentService.tweets("jsmith")
    }
}
