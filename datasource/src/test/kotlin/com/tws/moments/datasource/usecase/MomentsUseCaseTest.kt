package com.tws.moments.datasource.usecase

import com.tws.moments.datasource.api.entry.TweetBeanApi
import com.tws.moments.datasource.api.entry.UserBean
import com.tws.moments.datasource.repository.MomentRepository
import com.tws.moments.datasource.shared.data.TweetBean
import com.tws.moments.datasource.usecase.helpers.CoroutineDispatcherHelper
import com.tws.moments.datasource.usecase.helpers.IDispatcher
import com.tws.moments.datasource.utis.assert
import com.tws.moments.datasource.utis.assertFailure
import com.tws.moments.datasource.utis.assertInstance
import com.tws.moments.datasource.utis.assertNotEmpty
import com.tws.moments.datasource.utis.assertSize
import com.tws.moments.datasource.utis.assertSuccess
import com.tws.moments.datasource.utis.mockTweetBean
import com.tws.moments.datasource.utis.mockTweetBeanApi
import com.tws.moments.datasource.utis.mockTweetBeanApiCommented
import com.tws.moments.datasource.utis.mockTweetBeanApiError
import com.tws.moments.datasource.utis.mockTweetBeanApiUnknownError
import com.tws.moments.datasource.utis.mockTweetBeanCommented
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class MomentsUseCaseTest {

    private val iDispatcher: IDispatcher = CoroutineDispatcherHelper()
    private lateinit var momentRepository: MomentRepository
    private lateinit var momentsUseCase: MomentsUseCase

    @Before
    fun setup() {
        momentRepository = mockk<MomentRepository>()

        momentsUseCase = MomentsUseCaseImpl(
            iDispatcher = iDispatcher,
            repository = momentRepository,
        )
    }

    @Test
    fun `fetch user, throws exception, return null`() = runTest {
        coEvery {
            momentRepository.fetchUser()
        } throws Exception()

        val result = momentsUseCase.fetchUser()

        assertNull(result)
    }

    @Test
    fun `fetch user, retrieve user`() = runTest {
        val userName = "username"
        val nick = "nick"
        val avatar = "avatar"
        val profileImage = "profileImage"

        coEvery {
            momentRepository.fetchUser()
        } returns UserBean(
            username = userName,
            nick = nick,
            avatar = avatar,
            profileImage = profileImage,
        )

        with(momentsUseCase.fetchUser()!!) {
            assertEquals(userName, this.username)
            assertEquals(nick, this.nick)
            assertEquals(avatar, this.avatar)
            assertEquals(profileImage, this.profileImage)
        }
    }

    @Test
    fun `fetch tweets, throws exception, return null`() = runTest {
        coEvery {
            momentRepository.fetchTweets()
        } throws Exception()

        val result = momentsUseCase.fetchTweets()

        assertNull(result)
    }

    @Test
    fun `fetch tweets, retrieve empty lists`() = runTest {
        coEvery {
            momentRepository.fetchTweets()
        } returns listOf()

        momentsUseCase.fetchTweets().assertSize(size = 0)
    }

    @Test
    fun `fetch tweets, retrieve items, check mapping from API data to VIEW data`() = runTest {
        coEvery {
            momentRepository.fetchTweets()
        } returns arrayListOf(
            mockTweetBeanApi()
        )

        momentsUseCase.fetchTweets()
            .assertSize(size = 1)
            .assertInstance(TweetBean::class.java)
    }

    @Test
    fun `fetch tweets, retrieve items, check data`() = runTest {
        val firstTweetBean = mockTweetBeanApi(
            content = "content 1",
        )

        val secondTweetBean = mockTweetBeanApiCommented(
            content = "content 2",
        )

        coEvery {
            momentRepository.fetchTweets()
        } returns arrayListOf(
            firstTweetBean,
            secondTweetBean,
        )

        momentsUseCase.fetchTweets()
            .assertSize(size = 2)
            .assertInstance(TweetBean::class.java)
            .assert { tweetBean ->
                assertThat(tweetBean.content, `is`(firstTweetBean.content))
            }
            .assertInstance(type = TweetBean::class.java, index = 1)
            .assert(1) { tweetBean ->
                assertThat(tweetBean.content, `is`(secondTweetBean.content))

                secondTweetBean.comments
                    .assertNotEmpty()
                    .assert { commentBean ->
                        assertThat(
                            secondTweetBean.comments!!.first().content,
                            `is`(commentBean.content),
                        )
                    }
            }
    }

    @Test
    fun `fetch tweets, retrieve more items then the page limit, limit it by the page size`() =
        runTest {
            val apiResult = arrayListOf<TweetBeanApi>()

            for (i in 0..(PAGE_TWEET_COUNT * 2)) {
                apiResult.add(mockTweetBeanApi())
            }

            coEvery {
                momentRepository.fetchTweets()
            } returns apiResult

            momentsUseCase.fetchTweets().assertSize(size = PAGE_TWEET_COUNT)
        }

    @Test
    fun `fetch tweets, filter tweets, remove empty content`() = runTest {

        coEvery {
            momentRepository.fetchTweets()
        } returns arrayListOf(
            mockTweetBeanApi(content = null),
            mockTweetBeanApi(content = ""),
            mockTweetBeanApi(content = "There is something"),
        )

        momentsUseCase.fetchTweets().assertSize(size = 1)
    }

    @Test
    fun `fetch tweets, filter tweets, remove tweets with error`() = runTest {
        coEvery {
            momentRepository.fetchTweets()
        } returns arrayListOf(
            mockTweetBeanApi(),
            mockTweetBeanApiError(),
        )

        momentsUseCase.fetchTweets().assertSize(size = 1)
    }

    @Test
    fun `fetch tweets, filter tweets, remove tweets with unknown error`() = runTest {
        coEvery {
            momentRepository.fetchTweets()
        } returns arrayListOf(
            mockTweetBeanApi(),
            mockTweetBeanApiUnknownError(),
        )

        momentsUseCase.fetchTweets().assertSize(size = 1)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `load more tweets, page index smaller then zero`() = runTest {
        momentsUseCase.loadMoreTweets(-1).first()
    }

    @Test
    fun `load more tweets, page index higher then page count, return failure`() = runTest {
        momentsUseCase.loadMoreTweets(1000000).first().assertFailure()
    }

    @Test
    fun `load more tweets, return items inside the page count boundaries`() = runTest {
        mockAllTweets(size = 20)

        momentsUseCase.loadMoreTweets(pageIndex = 1)
            .first()
            .assertSuccess()
            .value!!
            .assertSize(PAGE_TWEET_COUNT)
    }

    @Test
    fun `load more tweets, mock tweets with error, returns empty`() = runTest {
        mockAllTweets(size = 20) {
            mockTweetBeanApiError()
        }

        momentsUseCase.loadMoreTweets(pageIndex = 1)
            .first()
            .assertSuccess()
            .value!!
            .assertSize(size = 0)
    }

    @Test
    fun `share comment, fails when comment is empty`() = runTest {
        momentsUseCase.shareComment(
            tweetBean = mockTweetBean(),
            comment = "",
        ).first().assertFailure()
    }

    @Test
    fun `share comment, create the first comment`() = runTest {
        val comment = "first comment"
        val tweetBean = mockTweetBean()

        momentsUseCase.shareComment(
            tweetBean = tweetBean,
            comment = comment,
        )
            .first()
            .assertSuccess()
            .value!!
            .assertSize(size = 1)
            .assert { commentBean ->
                assertThat(comment, `is`(commentBean.content))
            }

        tweetBean.comments.assertSize(size = 1)
    }

    @Test
    fun `share comment, add a new comment to a tweet already commented`() = runTest {
        val comment = "second comment"
        val tweetBean = mockTweetBeanCommented()

        momentsUseCase.shareComment(
            tweetBean = tweetBean,
            comment = comment,
        )
            .first()
            .assertSuccess()
            .value!!
            .assertSize(size = 2)
            .assert(index = 1) { commentBean ->
                assertThat(comment, `is`(commentBean.content))
            }

        tweetBean.comments.assertSize(size = 2)
    }

    private suspend fun mockAllTweets(size: Int, mock: (() -> TweetBeanApi)? = null) {
        val pageOneResult = arrayListOf<TweetBeanApi>()

        for (i in 0 until size) {
            pageOneResult.add(mock?.invoke() ?: mockTweetBeanApi())
        }

        coEvery {
            momentRepository.fetchTweets()
        } returns pageOneResult

        momentsUseCase.fetchTweets()
    }
}