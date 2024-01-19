package com.tws.moments.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tws.moments.api.entry.TweetBean
import com.tws.moments.usecase.MomentsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.min

private const val TAG = "MainViewModel##"
private const val PAGE_TWEET_COUNT = 5

@HiltViewModel
class MainViewModel @Inject constructor(
    private val useCase: MomentsUseCase,
) : ViewModel() {

    private val _uiState: MutableStateFlow<MainUiState> = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState>
        get() = _uiState

    private var reqPageIndex = 1

    fun onEvent(event: MainEvent) {
        when (event) {
            MainEvent.FetchTweets -> {
                loadTweets()
            }

            MainEvent.FetchUserBean -> {
                loadUserInfo()
            }

            MainEvent.FetchMoreTweets -> {
                fetchMoreTweets()
            }
        }
    }

    private fun fetchMoreTweets() {
        if (reqPageIndex <= pageCount - 1) {
            _uiState.update { it.copy(isFetchingMore = true) }

            Log.i(TAG, "internal load more")

            loadMoreTweets(reqPageIndex) { result ->
                result?.filter { it.noErrorAndWithContent() }?.also { filteredResult ->
                    reqPageIndex++

                    _uiState.update { state ->
                        state.copy(
                            tweets = arrayListOf<TweetBean>().apply {
                                state.tweets?.let { tweets -> addAll(tweets) }
                                addAll(filteredResult)
                            },
                            isFetchingMore = false,
                        )
                    }
                }
            }
        }
    }

    private fun loadUserInfo() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(userBean = useCase.fetchUser())
            }
        }
    }

    private fun loadTweets() {
        _uiState.update {
            it.copy(
                isRefreshing = true,
            )
        }

        viewModelScope.launch {
            val result = useCase.fetchTweets()

            val tweets = if ((result?.size ?: 0) > PAGE_TWEET_COUNT) {
                result?.subList(0, PAGE_TWEET_COUNT)
            } else {
                result
            }

            _uiState.update { state ->
                state.copy(
                    allTweets = result,
                    tweets = tweets?.filter { it.noErrorAndWithContent() },
                    isRefreshing = false,
                )
            }
        }
    }

    fun refreshTweets() {
        loadTweets()
    }

    private val pageCount: Int
        get() {
            return when {
                _uiState.value.allTweets.isNullOrEmpty() -> 0
                _uiState.value.allTweets!!.size % PAGE_TWEET_COUNT == 0 -> _uiState.value.allTweets!!.size / PAGE_TWEET_COUNT
                else -> _uiState.value.allTweets!!.size / PAGE_TWEET_COUNT + 1
            }
        }

    private fun loadMoreTweets(pageIndex: Int, onLoad: (List<TweetBean>?) -> Unit) {
        if (pageIndex < 0) {
            throw IllegalArgumentException("page index must greater than or equal to 0.")
        }

        if (pageIndex > pageCount - 1) {
            return
        }

        viewModelScope.launch {
            val startIndex = PAGE_TWEET_COUNT * pageIndex
            val endIndex = min(_uiState.value.allTweets!!.size, PAGE_TWEET_COUNT * (pageIndex + 1))
            val result = _uiState.value.allTweets!!.subList(startIndex, endIndex)
            onLoad(result)
        }
    }
}