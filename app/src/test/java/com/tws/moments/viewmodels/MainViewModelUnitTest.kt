package com.tws.moments.viewmodels

import app.cash.turbine.test
import com.tws.moments.datasource.test.mockTweetBean
import com.tws.moments.datasource.usecase.MomentsUseCase
import com.tws.moments.ui.main.MainEvent
import com.tws.moments.ui.main.MainViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.rules.TestWatcher
import org.junit.runner.Description


@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : TestWatcher() {
    override fun starting(description: Description?) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description?) {
        Dispatchers.resetMain()
    }
}

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
            awaitItem().also { state ->
                assertNull(state.tweets)
                assertFalse(state.isRefreshing)
                assertFalse(state.isFetchingMore)
            }

            mainViewModel.onEvent(
                MainEvent.FetchTweets
            )

            awaitItem().also { state ->
                assertNull(state.tweets)
                assertTrue(state.isRefreshing)
                assertFalse(state.isFetchingMore)
            }

            awaitItem().also { state ->
                assertNull(state.tweets)
                assertFalse(state.isRefreshing)
                assertFalse(state.isFetchingMore)
            }

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `fetch tweets, retrieve empty list`() = runTest {
        coEvery {
            momentUseCase.fetchTweets()
        } returns listOf()

        mainViewModel.uiState.test {
            awaitItem().also { state ->
                assertNull(state.tweets)
                assertFalse(state.isRefreshing)
                assertFalse(state.isFetchingMore)
            }

            mainViewModel.onEvent(
                MainEvent.FetchTweets
            )

            advanceUntilIdle()

            awaitItem().also { state ->
                assertEquals(0, state.tweets!!.size)
                assertFalse(state.isRefreshing)
                assertFalse(state.isFetchingMore)
            }

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
            awaitItem().also { state ->
                assertNull(state.tweets)
                assertFalse(state.isRefreshing)
                assertFalse(state.isFetchingMore)
            }

            mainViewModel.onEvent(
                MainEvent.FetchTweets
            )

            awaitItem().also { state ->
                assertNull(state.tweets)
                assertTrue(state.isRefreshing)
                assertFalse(state.isFetchingMore)
            }

            awaitItem().also { state ->
                assertEquals(1, state.tweets!!.size)
                assertEquals(contentTest, state.tweets!!.first().content)
                assertFalse(state.isRefreshing)
                assertFalse(state.isFetchingMore)
            }

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
            awaitItem().also { state ->
                assertNull(state.tweets)
                assertFalse(state.isRefreshing)
                assertFalse(state.isFetchingMore)
            }

            mainViewModel.onEvent(
                MainEvent.RefreshTweets
            )

            awaitItem().also { state ->
                assertNull(state.tweets)
                assertTrue(state.isRefreshing)
                assertFalse(state.isFetchingMore)
            }

            awaitItem().also { state ->
                assertNull(state.tweets)
                assertFalse(state.isRefreshing)
                assertFalse(state.isFetchingMore)
            }

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `refresh tweets, retrieve empty list`() = runTest {
        coEvery {
            momentUseCase.fetchTweets()
        } returns listOf()

        mainViewModel.uiState.test {
            awaitItem().also { state ->
                assertNull(state.tweets)
                assertFalse(state.isRefreshing)
                assertFalse(state.isFetchingMore)
            }

            mainViewModel.onEvent(
                MainEvent.RefreshTweets
            )

            advanceUntilIdle()

            awaitItem().also { state ->
                assertEquals(0, state.tweets!!.size)
                assertFalse(state.isRefreshing)
                assertFalse(state.isFetchingMore)
            }

            cancelAndConsumeRemainingEvents()
        }
    }
}