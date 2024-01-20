package com.tws.moments.viewmodels

import app.cash.turbine.test
import com.tws.moments.utils.mockTweetBean
import com.tws.moments.datasource.usecase.MomentsUseCase
import com.tws.moments.ui.main.MainEvent
import com.tws.moments.ui.main.MainUiState
import com.tws.moments.ui.main.MainViewModel
import com.tws.moments.utils.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
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
            awaitItem().also { state ->
                state.assertIdleState()
            }

            mainViewModel.onEvent(
                MainEvent.RefreshTweets
            )

            advanceUntilIdle()

            awaitItem().assertFetchTweetsResult(0)

            cancelAndConsumeRemainingEvents()
        }
    }

    private fun MainUiState.assertIdleState() {
        assertNull(tweets)
        assertFalse(isRefreshing)
        assertFalse(isFetchingMore)
    }

    private fun MainUiState.assertFetchingTweets() {
        assertNull(tweets)
        assertTrue(isRefreshing)
        assertFalse(isFetchingMore)
    }

    private fun MainUiState.assertFetchTweetsResultNull() {
        assertNull(tweets)
        assertFalse(isRefreshing)
        assertFalse(isFetchingMore)
    }

    private fun MainUiState.assertFetchTweetsResult(size: Int) {
        assertEquals(size, tweets!!.size)
        assertFalse(isRefreshing)
        assertFalse(isFetchingMore)
    }

    private fun MainUiState.assertFetchTweetsResultContent(size: Int, content: String, index: Int) {
        assertEquals(size, tweets!!.size)
        assertEquals(content, tweets!![index].content)
        assertFalse(isRefreshing)
        assertFalse(isFetchingMore)
    }
}