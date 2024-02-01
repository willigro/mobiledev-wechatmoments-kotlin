package com.tws.moments.ui.main

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tws.moments.datasource.shared.data.TweetBean
import com.tws.moments.datasource.usecase.MomentsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val INITIAL_PAGE_INDEX = 1

@HiltViewModel
class MainViewModel @Inject constructor(
    private val useCase: MomentsUseCase,
) : ViewModel() {

    private val _uiEvent: MutableSharedFlow<List<String>?> = MutableSharedFlow()
    val uiEvent: SharedFlow<List<String>?>
        get() = _uiEvent

    private val _uiState: MutableStateFlow<MainUiState> = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState>
        get() = _uiState

    private var reqPageIndex = INITIAL_PAGE_INDEX

    fun onEvent(event: MainEvent) {
        when (event) {
            MainEvent.FetchTweets -> {
                loadTweets()
            }

            MainEvent.RefreshTweets -> {
                refreshTweets()
            }

            MainEvent.FetchUserBean -> {
                loadUserInfo()
            }

            MainEvent.FetchMoreTweets -> {
                fetchMoreTweets()
            }

            is MainEvent.ShareNewComment -> {
                shareNewComment(event.tweetBean, event.comment)
            }

            is MainEvent.OpenImage -> {
                viewModelScope.launch {
                    _uiEvent.emit(event.url)
                }
            }

            MainEvent.ClosesImage -> {
                viewModelScope.launch {
                    _uiEvent.emit(null)
                }
            }
        }
    }

    private fun shareNewComment(tweetBean: TweetBean, comment: String) {
        viewModelScope.launch {
            // If for some reason the index change, just try getting the index before each update
            val indexOfTweet = _uiState.value.tweets?.indexOfFirst { it.id == tweetBean.id }

            if (indexOfTweet != null && indexOfTweet > -1) {
                _uiState.update { state ->
                    state.copy(
                        tweets = state.tweets?.apply {
                            set(indexOfTweet, tweetBean.copy(isSendingComment = true))
                        }
                    )
                }

                useCase.shareComment(
                    tweetBean = tweetBean,
                    comment = comment,
                ).collect {
                    _uiState.update { state ->
                        state.copy(
                            tweets = state.tweets?.apply {
                                set(
                                    indexOfTweet,
                                    state.tweets[indexOfTweet].copy(isSendingComment = false)
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    private fun loadUserInfo() {
        viewModelScope.launch {
            val userBean = useCase.fetchUser()

            _uiState.update {
                it.copy(userBean = userBean)
            }
        }
    }

    private fun loadTweets() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isRefreshing = true,
                    isFetchingMore = false,
                )
            }

            val result = useCase.fetchTweets()

            _uiState.update { state ->
                state.copy(
                    tweets = result?.toMutableStateList(),
                    isRefreshing = false,
                    isLoading = false,
                    hasErrorOnTweets = result == null,
                )
            }
        }
    }

    private fun fetchMoreTweets() {
        viewModelScope.launch {
            _uiState.update { it.copy(isFetchingMore = true) }

            useCase.loadMoreTweets(reqPageIndex).collectLatest { resultUC ->
                val result = resultUC.getOrNull()

                if (result == null) {
                    _uiState.update { state ->
                        state.copy(
                            isFetchingMore = false,
                        )
                    }
                } else {
                    reqPageIndex++

                    _uiState.update { state ->
                        state.copy(
                            tweets = mutableStateListOf<TweetBean>().apply {
                                state.tweets?.let { tweets -> addAll(tweets) }
                                addAll(result)
                            },
                            isFetchingMore = false,
                        )
                    }
                }
            }
        }
    }

    private fun refreshTweets() {
        reqPageIndex = INITIAL_PAGE_INDEX
        loadTweets()
    }
}