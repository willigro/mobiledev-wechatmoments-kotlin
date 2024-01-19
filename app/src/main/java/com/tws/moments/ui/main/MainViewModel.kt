package com.tws.moments.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tws.moments.datasource.api.entry.CommentsBean
import com.tws.moments.datasource.api.entry.SenderBean
import com.tws.moments.datasource.api.entry.TweetBean
import com.tws.moments.datasource.usecase.MomentsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "MainViewModel##"
private const val INITIAL_PAGE_INDEX =  1

@HiltViewModel
class MainViewModel @Inject constructor(
    private val useCase: MomentsUseCase,
) : ViewModel() {

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
        }
    }

    private fun shareNewComment(tweetBean: TweetBean, comment: String) {
        val commentBean = CommentsBean(
            content = comment,
            sender = SenderBean("nick", null, null),
        )

        _uiState.update { state ->
            state.copy(
                tweets = state.tweets?.toMutableList()?.apply {
                    val index = indexOfFirst { it.id == tweetBean.id }

                    if (index > -1) {
                        if (tweetBean.comments == null) {
                            this[index] = this[index].copy(
                                comments = arrayListOf(commentBean)
                            )
                        } else {
                            this[index] = this[index].copy(
                                comments = arrayListOf<CommentsBean>().apply {
                                    if (tweetBean.comments.isNullOrEmpty().not()) {
                                        addAll(tweetBean.comments!!)
                                    }
                                    add(commentBean)
                                }
                            )
                        }
                    }
                }
            )
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
                isFetchingMore = false,
            )
        }

        viewModelScope.launch {
            val result = useCase.fetchTweets()

            _uiState.update { state ->
                state.copy(
                    tweets = result,
                    isRefreshing = false,
                )
            }
        }
    }

    private fun fetchMoreTweets() {
        _uiState.update { it.copy(isFetchingMore = true) }

        viewModelScope.launch {
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
                            tweets = arrayListOf<TweetBean>().apply {
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

    fun refreshTweets() {
        reqPageIndex = INITIAL_PAGE_INDEX
        loadTweets()
    }
}