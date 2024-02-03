package com.tws.moments.viewmodels

import app.cash.turbine.test
import com.tws.moments.datasource.usecase.MomentsUseCase
import com.tws.moments.datasource.usecase.helpers.ResultUC
import com.tws.moments.datasource.usecase.helpers.fails
import com.tws.moments.ui.main.MainEvent
import com.tws.moments.ui.main.MainViewModel
import com.tws.moments.ui.navigation.AppNavigator
import com.tws.moments.utils.MainDispatcherRule
import com.tws.moments.utils.mockCommentBean
import com.tws.moments.utils.mockTweetBean
import com.tws.moments.utils.mockUserBean
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

// TODO (rittmann) I don't like using delay on tests, see a better way to await the State the change
//  it is need to prevent that the initial state is not notified since the next one is updated too fast
private const val DELAY_TO_UPDATE_STATE = 100L

@ExperimentalCoroutinesApi
class MainViewModelUnitTest {

    @Rule
    @JvmField
    var rule: TestRule = MainDispatcherRule()

    private lateinit var momentUseCase: MomentsUseCase
    private lateinit var navigator: AppNavigator
    private lateinit var mainViewModel: MainViewModel

    @Before
    fun setUp() {
        navigator = mockk<AppNavigator>()
        momentUseCase = mockk<MomentsUseCase>()

        setupEmptyAnswers()
    }

    private fun setupEmptyAnswers() {
        coEvery { momentUseCase.fetchTweets() } returns null
        coEvery { momentUseCase.fetchUser() } returns null
    }

    @Test
    fun `fetch tweets at the init, retrieve null`() = runTest {
        coEvery {
            momentUseCase.fetchTweets()
        } coAnswers {
            delay(DELAY_TO_UPDATE_STATE)
            null
        }

        mainViewModel = MainViewModel(navigator, momentUseCase)

        mainViewModel.uiState.test {
            awaitItem().assertFetchingTweets()

            awaitItem().assertFetchTweetsResultNull()

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `fetch tweets at the init, retrieve empty list`() = runTest {
        coEvery {
            momentUseCase.fetchTweets()
        } returns listOf()

        mainViewModel = MainViewModel(navigator, momentUseCase)

        mainViewModel.uiState.test {
            advanceUntilIdle()

            awaitItem().assertFetchTweetsResult(0)

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `fetch tweet at the init, delayed, retrieve populated list`() = runTest {
        val contentTest = "Content 1"

        coEvery {
            momentUseCase.fetchTweets()
        } coAnswers {
            delay(DELAY_TO_UPDATE_STATE)
            listOf(
                mockTweetBean(contentTest)
            )
        }

        mainViewModel = MainViewModel(navigator, momentUseCase)

        mainViewModel.uiState.test {
            awaitItem().assertFetchingTweets()

            awaitItem().assertFetchTweetsResultContent(1, contentTest, 0)

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `refresh tweets, retrieve null`() = runTest {
        coEvery {
            momentUseCase.fetchTweets()
        } coAnswers {
            delay(DELAY_TO_UPDATE_STATE)
            null
        }

        mainViewModel = MainViewModel(navigator, momentUseCase)

        mainViewModel.uiState.test {
            mainViewModel.onEvent(
                MainEvent.RefreshTweets
            )

            awaitItem().assertFetchingTweets()

            awaitItem().assertFetchTweetsResultNull()

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `refresh tweets, retrieve empty list`() = runTest {
        coEvery {
            momentUseCase.fetchTweets()
        } returns listOf()

        mainViewModel = MainViewModel(navigator, momentUseCase)

        mainViewModel.uiState.test {
            mainViewModel.onEvent(
                MainEvent.RefreshTweets
            )

            advanceUntilIdle()

            awaitItem().assertFetchTweetsResult(0)

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `fetch user bean at the init, retrieve null`() = runTest {
        coEvery {
            momentUseCase.fetchUser()
        } returns null

        mainViewModel = MainViewModel(navigator, momentUseCase)

        mainViewModel.uiState.test {
            mainViewModel.uiState.value.assertUserInfoNull()

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `fetch user bean at the init, retrieve valid user`() = runTest {
        val userName = "userName"

        coEvery {
            momentUseCase.fetchUser()
        } returns mockUserBean(userName)

        mainViewModel = MainViewModel(navigator, momentUseCase)

        mainViewModel.uiState.test {
            advanceUntilIdle()

            awaitItem().assertUserBean(userName)

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `fetch more tweets, load next page, starts with empty list, retrieve tweets`() = runTest {
        val pageTwo = 1

        coEvery {
            momentUseCase.loadMoreTweets(pageTwo)
        } returns flow {
            delay(DELAY_TO_UPDATE_STATE)
            emit(
                ResultUC.success(
                    arrayListOf(
                        mockTweetBean("list 2, content 1"),
                        mockTweetBean("list 2, content 2"),
                    )
                )
            )
        }

        mainViewModel = MainViewModel(navigator, momentUseCase)

        mainViewModel.uiState.test {
            awaitItem()

            mainViewModel.onEvent(
                MainEvent.FetchMoreTweets
            )

            awaitItem().assertFetchingMoreTweets()

            awaitItem().assertFetchingMoreTweetsConcluded(size = 2)

            cancelAndConsumeRemainingEvents()
        }
    }

    // TODO (rittmann) to make it simply I could inject the UiState, should worth it?
    @Test
    fun `fetch more tweets, load next page, starts with populated list, retrieve tweets`() =
        runTest {
            val nextPage = 1

            coEvery {
                momentUseCase.loadMoreTweets(nextPage)
            } returns flow {
                emit(
                    ResultUC.success(
                        arrayListOf(
                            mockTweetBean("list 2, content 1"),
                            mockTweetBean("list 2, content 2"),
                        )
                    )
                )
            }

            loadInitialTweetAndAdvance(
                appNavigator = navigator,
                momentUseCase = momentUseCase,
            ) { state, mainViewModel ->
                mainViewModel.onEvent(
                    MainEvent.FetchMoreTweets
                )

                awaitItem().assertFetchingMoreTweets()

                awaitItem().assertFetchingMoreTweetsConcluded(size = 3)

                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `fetch more tweets, load next page, starts with populated list, retrieve null`() = runTest {
        val nextPage = 1

        coEvery {
            momentUseCase.loadMoreTweets(nextPage)
        } returns flow {
            emit(
                ResultUC.success(
                    null
                )
            )
        }

        loadInitialTweetAndAdvance(
            appNavigator = navigator,
            momentUseCase = momentUseCase,
        ) { state, mainViewModel ->
            mainViewModel.onEvent(
                MainEvent.FetchMoreTweets
            )

            awaitItem().assertFetchingMoreTweets()

            awaitItem().assertFetchingMoreTweetsConcluded(size = 1)

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `fetch more tweets, load next page, starts with populated list, retrieve error`() =
        runTest {
            val nextPage = 1

            coEvery {
                momentUseCase.loadMoreTweets(nextPage)
            } returns flow {
                fails()
            }

            loadInitialTweetAndAdvance(
                appNavigator = navigator,
                momentUseCase = momentUseCase,
            ) { state, mainViewModel ->
                mainViewModel.onEvent(
                    MainEvent.FetchMoreTweets
                )

                awaitItem().assertFetchingMoreTweets()

                awaitItem().assertFetchingMoreTweetsConcluded(size = 1)

                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `share comment, create valid comment to a tweet without comments`() = runTest {
        loadInitialTweetAndAdvance(
            appNavigator = navigator,
            momentUseCase = momentUseCase,
        ) { state, mainViewModel ->
            val firstTweet = mainViewModel.uiState.value.tweets!!.first()
            val newComment = "New comment"

            coEvery {
                momentUseCase.shareComment(
                    firstTweet,
                    newComment,
                )
            } returns flow {
                delay(DELAY_TO_UPDATE_STATE)
                emit(
                    ResultUC.success(
                        listOf(
                            mockCommentBean(newComment)
                        )
                    )
                )
            }

            mainViewModel.onEvent(
                MainEvent.ShareNewComment(
                    tweetBean = firstTweet,
                    comment = newComment,
                )
            )

            state.assertSendingComment(index = 0)

            delay(DELAY_TO_UPDATE_STATE)

            state.assertNewCommentDone(
                index = 0,
            )

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `open image`() = runTest {
        mainViewModel = MainViewModel(navigator, momentUseCase)

        mainViewModel.uiEvent.test {
            mainViewModel.onEvent(MainEvent.OpenImage(listOf("not empty url")))

            with(awaitItem()) {
                assertThat(this!!.size, `is`(1))
            }
        }
    }

    @Test
    fun `closes image`() = runTest {
        mainViewModel = MainViewModel(navigator, momentUseCase)

        mainViewModel.uiEvent.test {
            mainViewModel.onEvent(MainEvent.OpenImage(listOf("not empty url")))

            with(awaitItem()) {
                assertThat(this!!.size, `is`(1))
            }

            mainViewModel.onEvent(MainEvent.ClosesImage)

            with(awaitItem()) {
                assertNull(this)
            }
        }
    }
}