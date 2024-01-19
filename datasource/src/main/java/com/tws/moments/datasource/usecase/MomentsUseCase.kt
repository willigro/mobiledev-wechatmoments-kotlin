package com.tws.moments.datasource.usecase

import com.tws.moments.datasource.api.entry.TweetBean
import com.tws.moments.datasource.api.entry.UserBean
import com.tws.moments.datasource.repository.MomentRepository
import com.tws.moments.datasource.usecase.helpers.ResultUC
import com.tws.moments.datasource.usecase.helpers.fails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.min


interface MomentsUseCase {
    suspend fun fetchUser(): UserBean?
    suspend fun fetchTweets(): List<TweetBean>?
    suspend fun loadMoreTweets(pageIndex: Int): Flow<ResultUC<List<TweetBean>?>>
}

private const val PAGE_TWEET_COUNT = 5

class MomentsUseCaseImpl @Inject constructor(
    private val repository: MomentRepository,
) : MomentsUseCase {

    private var allTweets: List<TweetBean>? = null

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
        allTweets = try {
            repository.fetchTweets()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

        return withContext(Dispatchers.Default) {
            val tweets = if ((allTweets?.size ?: 0) > PAGE_TWEET_COUNT) {
                allTweets?.subList(0, PAGE_TWEET_COUNT)
            } else {
                allTweets
            }

            tweets?.filter { it.noErrorAndWithContent() }
        }
    }

    override suspend fun loadMoreTweets(
        pageIndex: Int,
    ): Flow<ResultUC<List<TweetBean>?>> = flow {
        if (pageIndex < 0) {
            throw IllegalArgumentException("page index must greater than or equal to 0.")
        }

        if (pageIndex > pageCount - 1 || allTweets.isNullOrEmpty()) {
            fails()
            return@flow
        }

        val startIndex = PAGE_TWEET_COUNT * pageIndex
        val endIndex = min(allTweets!!.size, PAGE_TWEET_COUNT * (pageIndex + 1))
        val result = allTweets!!.subList(startIndex, endIndex).filter { it.noErrorAndWithContent() }

        emit(ResultUC.success(result))
    }

    private val pageCount: Int
        get() {
            return when {
                allTweets.isNullOrEmpty() -> 0
                allTweets!!.size % PAGE_TWEET_COUNT == 0 -> allTweets!!.size / PAGE_TWEET_COUNT
                else -> allTweets!!.size / PAGE_TWEET_COUNT + 1
            }
        }
}