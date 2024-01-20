package com.tws.moments.viewmodels

import app.cash.turbine.test
import com.tws.moments.utils.mockTweetBean
import com.tws.moments.datasource.usecase.MomentsUseCase
import com.tws.moments.datasource.usecase.helpers.ResultUC
import com.tws.moments.datasource.usecase.helpers.fails
import com.tws.moments.ui.main.MainEvent
import com.tws.moments.ui.main.MainViewModel
import com.tws.moments.utils.MainDispatcherRule
import com.tws.moments.utils.mockUserBean
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
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
    private lateinit var mainViewModel: MainViewModel

    @Before
    fun setUp() {
        momentUseCase = mockk<MomentsUseCase>()
        mainViewModel = MainViewModel(momentUseCase)
    }

    @Test
    fun `fetch tweets, retrieve null`() = runTest {
        coEvery {
            momentUseCase.fetchTweets()
        } coAnswers {
            delay(DELAY_TO_UPDATE_STATE)
            null
        }

        mainViewModel.uiState.test {
            awaitItem().assertIdleState()

            mainViewModel.onEvent(
                MainEvent.FetchTweets
            )

            awaitItem().assertFetchingTweets()

            awaitItem().assertFetchTweetsResultNull()

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `fetch tweets, retrieve empty list`() = runTest {
        coEvery {
            momentUseCase.fetchTweets()
        } returns listOf()

        mainViewModel.uiState.test {
            awaitItem().assertIdleState()

            mainViewModel.onEvent(
                MainEvent.FetchTweets
            )

            advanceUntilIdle()

            awaitItem().assertFetchTweetsResult(0)

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `fetch tweet, delayed, retrieve populated list`() = runTest {
        val contentTest = "Content 1"

        coEvery {
            momentUseCase.fetchTweets()
        } coAnswers {
            delay(DELAY_TO_UPDATE_STATE)
            listOf(
                mockTweetBean(contentTest)
            )
        }

        mainViewModel.uiState.test {
            awaitItem().assertIdleState()

            mainViewModel.onEvent(
                MainEvent.FetchTweets
            )

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

        mainViewModel.uiState.test {
            awaitItem().assertIdleState()

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

        mainViewModel.uiState.test {
            awaitItem().assertIdleState()

            mainViewModel.onEvent(
                MainEvent.RefreshTweets
            )

            advanceUntilIdle()

            awaitItem().assertFetchTweetsResult(0)

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `fetch user bean, retrieve null`() = runTest {
        coEvery {
            momentUseCase.fetchUser()
        } returns null

        mainViewModel.uiState.test {
            awaitItem().assertIdleState()

            mainViewModel.onEvent(
                MainEvent.FetchUserBean
            )

            expectNoEvents()

            mainViewModel.uiState.value.assertUserInfoNull()

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `fetch user bean, retrieve valid user`() = runTest {
        val userName = "userName"

        coEvery {
            momentUseCase.fetchUser()
        } returns mockUserBean(userName)

        mainViewModel.uiState.test {
            awaitItem().assertIdleState()

            mainViewModel.onEvent(
                MainEvent.FetchUserBean
            )

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
            emit(
                ResultUC.success(
                    arrayListOf(
                        mockTweetBean("list 2, content 1"),
                        mockTweetBean("list 2, content 2"),
                    )
                )
            )
        }

        mainViewModel.uiState.test {
            awaitItem().assertIdleState()

            mainViewModel.onEvent(
                MainEvent.FetchMoreTweets
            )

            awaitItem().assertFetchingMoreTweets()

            awaitItem().assertFetchingMoreTweetsConcluded(2)

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
                mainViewModel = mainViewModel,
                momentUseCase = momentUseCase,
            ) {
                mainViewModel.onEvent(
                    MainEvent.FetchMoreTweets
                )

                awaitItem().assertFetchingMoreTweets()

                awaitItem().assertFetchingMoreTweetsConcluded(3)

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
            mainViewModel = mainViewModel,
            momentUseCase = momentUseCase,
        ) {
            mainViewModel.onEvent(
                MainEvent.FetchMoreTweets
            )

            awaitItem().assertFetchingMoreTweets()

            awaitItem().assertFetchingMoreTweetsConcluded(1)

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
                mainViewModel = mainViewModel,
                momentUseCase = momentUseCase,
            ) {
                mainViewModel.onEvent(
                    MainEvent.FetchMoreTweets
                )

                awaitItem().assertFetchingMoreTweets()

                awaitItem().assertFetchingMoreTweetsConcluded(1)

                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `share comment`() {

    }
}