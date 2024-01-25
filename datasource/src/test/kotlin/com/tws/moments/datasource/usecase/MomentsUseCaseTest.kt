package com.tws.moments.datasource.usecase

import com.tws.moments.datasource.api.entry.TweetBeanApi
import com.tws.moments.datasource.api.entry.UserBean
import com.tws.moments.datasource.repository.MomentRepository
import com.tws.moments.datasource.usecase.helpers.CoroutineDispatcherHelper
import com.tws.moments.datasource.usecase.helpers.IDispatcher
import com.tws.moments.datasource.utis.assert
import com.tws.moments.datasource.utis.assertInstance
import com.tws.moments.datasource.utis.assertNotEmpty
import com.tws.moments.datasource.utis.assertSize
import com.tws.moments.datasource.utis.mockTweetBeanApi
import com.tws.moments.datasource.utis.mockTweetBeanApiCommented
import com.tws.moments.datasource.utis.mockTweetBeanApiError
import com.tws.moments.datasource.utis.mockTweetBeanApiUnknownError
import io.mockk.coEvery
import io.mockk.mockk
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
            .assertInstance()
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
            .assertInstance()
            .assert { tweetBean ->
                assertThat(tweetBean.content, `is`(firstTweetBean.content))
            }
            .assertInstance(1)
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
}