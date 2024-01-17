package com.tws.moments.usecase

import com.tws.moments.api.entry.TweetBean
import com.tws.moments.api.entry.UserBean
import com.tws.moments.repository.MomentRepository
import javax.inject.Inject


interface MomentsUseCase {
    suspend fun fetchUser(): UserBean?
    suspend fun fetchTweets(): List<TweetBean>?
}

class MomentsUseCaseImpl @Inject constructor(
    private val repository: MomentRepository,
) : MomentsUseCase {

    override suspend fun fetchUser(): UserBean? {
        val result = try {
            repository.fetchUser()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

        return result
    }

    override suspend fun fetchTweets(): List<TweetBean>? {
        val result = try {
            repository.fetchTweets()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

        return result
    }
}