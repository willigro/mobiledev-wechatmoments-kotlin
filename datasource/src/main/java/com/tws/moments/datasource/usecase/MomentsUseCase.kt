package com.tws.moments.datasource.usecase

import androidx.annotation.VisibleForTesting
import com.tws.moments.core.file.Image
import com.tws.moments.datasource.api.entry.CommentsBean
import com.tws.moments.datasource.api.entry.SenderBean
import com.tws.moments.datasource.api.entry.UserBean
import com.tws.moments.datasource.mappers.mapToTweetBean
import com.tws.moments.datasource.repository.MomentRepository
import com.tws.moments.datasource.shared.data.StableList
import com.tws.moments.datasource.shared.data.TweetBean
import com.tws.moments.datasource.usecase.helpers.IDispatcher
import com.tws.moments.datasource.usecase.helpers.ResultUC
import com.tws.moments.datasource.usecase.helpers.fails
import com.tws.moments.datasource.utils.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.min


interface MomentsUseCase {
    suspend fun fetchUser(): UserBean?
    suspend fun fetchTweets(): List<TweetBean>?
    suspend fun loadMoreTweets(pageIndex: Int): Flow<ResultUC<List<TweetBean>?>>
    suspend fun shareComment(
        tweetBean: TweetBean,
        comment: String,
    ): Flow<ResultUC<List<CommentsBean>?>>

    suspend fun createTweet(content: String, result: Image): Flow<ResultUC<Boolean>>
}

@VisibleForTesting
const val PAGE_TWEET_COUNT = 5

class MomentsUseCaseImpl @Inject constructor(
    private val iDispatcher: IDispatcher,
    private val repository: MomentRepository,
    private val dateUtils: DateUtils,
) : MomentsUseCase {

    private var allTweets: List<TweetBean>? = null
    private var localAllTweets: ArrayList<TweetBean> = arrayListOf()

    override suspend fun fetchUser(): UserBean? {
        val result = try {
            repository.fetchUser()
        } catch (e: Exception) {
            null
        }

        return result
    }

    override suspend fun fetchTweets(): List<TweetBean>? {
        allTweets = try {
            repository.fetchTweets().mapToTweetBean(dateUtils).let {
                arrayListOf<TweetBean>().apply {
                    addAll(localAllTweets)
                    it?.also {
                        this.addAll(it)
                    }
                }
            }
        } catch (e: Exception) {
            null
        }

        return withContext(iDispatcher.dispatcherDefault()) {
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

    override suspend fun shareComment(
        tweetBean: TweetBean,
        comment: String,
    ): Flow<ResultUC<List<CommentsBean>?>> = flow {
        if (comment.isEmpty()) {
            fails()
            return@flow
        }

        val commentBean = CommentsBean(
            content = comment,
            sender = SenderBean("nick", null, null),
        )

        tweetBean.comments.add(commentBean)

        emit(ResultUC.success(tweetBean.comments))
    }.flowOn(iDispatcher.dispatcherDefault())

    override suspend fun createTweet(
        content: String, result: Image
    ): Flow<ResultUC<Boolean>> = flow {
        try {
            val bean = TweetBean(
                content = content,
                sender = SenderBean("nick", null, null),
                imagesUrls = StableList(listOf(result.uri.toString()))
            )

            localAllTweets.add(0, bean)

            if (allTweets.isNullOrEmpty()) {
                allTweets = listOf(bean)
            } else {
                (allTweets as ArrayList).add(0, bean)
            }

            emit(
                ResultUC.success()
            )
        } catch (e: Exception) {
            fails()
        }
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