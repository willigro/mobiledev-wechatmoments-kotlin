package com.tws.moments.datasource.repository

import com.tws.moments.datasource.api.MomentService
import com.tws.moments.datasource.api.entry.TweetBeanServer
import com.tws.moments.datasource.api.entry.UserBean
import com.tws.moments.datasource.usecase.helpers.IDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface MomentRepository {
    suspend fun fetchUser(): UserBean
    suspend fun fetchTweets(): List<TweetBeanServer>
}

class MomentRepositoryImpl @Inject constructor(
    private val iDispatcher: IDispatcher,
    private val momentService: MomentService,
    // TODO (rittmann) add a cache, can be a DAO, Preferences, File, anything
) : MomentRepository {
    override suspend fun fetchUser(): UserBean = withContext(iDispatcher.dispatcherIO()) {
        momentService.user("jsmith")
    }

    override suspend fun fetchTweets(): List<TweetBeanServer> = withContext(iDispatcher.dispatcherIO()) {
        momentService.tweets("jsmith")
    }
}
