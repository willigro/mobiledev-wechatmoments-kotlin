package com.tws.moments.viewmodels

import app.cash.turbine.TurbineTestContext
import app.cash.turbine.test
import com.tws.moments.datasource.usecase.MomentsUseCase
import com.tws.moments.ui.main.MainEvent
import com.tws.moments.ui.main.MainUiState
import com.tws.moments.ui.main.MainViewModel
import com.tws.moments.utils.mockTweetBean
import io.mockk.coEvery
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue

internal fun MainUiState.assertIdleState() {
    assertNull(tweets)
    assertFalse(isRefreshing)
    assertFalse(isFetchingMore)
    assertTrue(isLoading)
    assertFalse(hasErrorOnTweets)
}

internal fun MainUiState.assertFetchingTweets() {
    assertNull(tweets)
    assertTrue(isRefreshing)
    assertFalse(isFetchingMore)
    assertFalse(hasErrorOnTweets)
}

internal fun MainUiState.assertFetchingMoreTweets() {
    assertFalse(isRefreshing)
    assertTrue(isFetchingMore)
}

internal fun MainUiState.assertFetchingMoreTweetsConcluded(size: Int) {
    assertEquals(size, tweets!!.size)
    assertFalse(isRefreshing)
    assertFalse(isFetchingMore)
}

internal fun MainUiState.assertFetchTweetsResultNull() {
    assertNull(tweets)
    assertTrue(hasErrorOnTweets)
    assertFalse(isRefreshing)
    assertFalse(isFetchingMore)
    assertFalse(isLoading)
}

internal fun MainUiState.assertFetchTweetsResult(size: Int) {
    assertEquals(size, tweets!!.size)
    assertFalse(isRefreshing)
    assertFalse(isFetchingMore)
    assertFalse(isLoading)
}

internal fun MainUiState.assertFetchTweetsResultContent(
    size: Int,
    content: String,
    index: Int,
): MainUiState {
    assertEquals(size, tweets!!.size)
    assertEquals(content, tweets!![index].content)
    assertFalse(isRefreshing)
    assertFalse(isFetchingMore)
    assertFalse(isLoading)
    return this
}

internal fun MainUiState.assertUserInfoNull() {
    assertNull(userBean)
}

internal fun MainUiState.assertUserBean(username: String) {
    assertEquals(username, userBean!!.username)
}

internal fun MainUiState.assertSendingComment(index: Int) {
    assertTrue(tweets!![index].isSendingComment)
}

internal fun MainUiState.assertNewCommentDone(index: Int) {
    assertFalse(tweets!![index].isSendingComment)
}

@OptIn(ExperimentalCoroutinesApi::class)
internal suspend fun TestScope.loadInitialTweetAndAdvance(
    mainViewModel: MainViewModel,
    momentUseCase: MomentsUseCase,
    validate: suspend TurbineTestContext<MainUiState>.(MainUiState) -> Unit,
) {
    mainViewModel.uiState.test {
        val initialTweets = arrayListOf(
            mockTweetBean("initial tweet")
        )

        coEvery {
            momentUseCase.fetchTweets()
        } returns initialTweets

        awaitItem().assertIdleState()

        mainViewModel.onEvent(
            MainEvent.FetchTweets
        )

        advanceUntilIdle()

        val state = awaitItem().assertFetchTweetsResultContent(1, initialTweets.first().content!!, 0)

        validate(state)
    }
}

