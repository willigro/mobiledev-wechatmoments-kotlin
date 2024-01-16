package com.tws.moments.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.tws.moments.R
import com.tws.moments.api.entry.CommentsBean
import com.tws.moments.api.entry.TweetBean
import com.tws.moments.designsystem.components.DivisorHorizontal
import com.tws.moments.designsystem.theme.AppTheme
import com.tws.moments.viewmodels.MainEvent
import com.tws.moments.viewmodels.MainUiState
import com.tws.moments.viewmodels.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MainScreenRoot(
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState().value

    LaunchedEffect(Unit) {
        viewModel.onEvent(
            MainEvent.FetchUserBean
        )
    }

    LaunchedEffect(Unit) {
        viewModel.onEvent(
            MainEvent.FetchTweets
        )
    }

    MainScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
    )
}

@Composable
private fun MainScreen(
    uiState: MainUiState,
    onEvent: (MainEvent) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val swipeRefreshState = rememberSwipeRefreshState(uiState.isRefreshing)

    SwipeRefresh(
        modifier = Modifier.fillMaxSize(),
        state = swipeRefreshState,
        onRefresh = {
            coroutineScope.launch {
                swipeRefreshState.isRefreshing = true

                delay(1000)

                onEvent(
                    MainEvent.FetchTweets
                )
            }
        },
    ) {
        LazyColumn {
            items(uiState.tweets.orEmpty()) { tweet ->
                BaseTweetComponent(tweetBean = tweet)
            }
        }
    }
}

@Composable
private fun BaseTweetComponent(
    tweetBean: TweetBean,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        ConstraintLayout(
            modifier = Modifier.fillMaxWidth(),
        ) {
            val (senderAvatar, senderNickname, tweetContent, comments) = createRefs()

            SubcomposeAsyncImage(
                modifier = Modifier
                    .constrainAs(senderAvatar) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                    }
                    .size(AppTheme.dimensions.baseTweet.avatarSize),
                model = tweetBean.sender?.avatar,
                loading = {
                    Box(
                        modifier = Modifier
                            .size(AppTheme.dimensions.baseTweet.avatarSize)
                            .background(Color.LightGray)
                    )
                },
                contentDescription = stringResource(R.string.content_description_sender_avatar_image)
            )

            Text(
                text = tweetBean.sender?.nick.orEmpty(),
                modifier = Modifier
                    .constrainAs(senderNickname) {
                        top.linkTo(senderAvatar.top)
                        start.linkTo(senderAvatar.end)
                    }
                    .padding(
                        start = AppTheme.dimensions.paddingSpaceBetweenComponentsSmallX,
                        top = AppTheme.dimensions.baseTweet.paddingTopSendNickname,
                    )
            )

            Column(
                modifier = Modifier
                    .constrainAs(tweetContent) {
                        top.linkTo(senderNickname.bottom)
                        start.linkTo(senderNickname.start)
                        end.linkTo(parent.end)
                    }
                    .padding(
                        end = AppTheme.dimensions.paddingSpaceBetweenComponentsMedium,
                    )
            ) {
                Text(
                    text = tweetBean.content.orEmpty(),
                    maxLines = 5,
                )
            }

            Column(
                modifier = Modifier
                    .constrainAs(comments) {
                        top.linkTo(tweetContent.bottom)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(tweetContent.start)
                        end.linkTo(parent.end)
                    }
                    .padding(
                        top = AppTheme.dimensions.paddingSpaceBetweenComponentsSmallX,
                    ),

                ) {
                tweetBean.comments?.forEach { comments ->
                    CommentComponent(commentsBean = comments)
                }
            }
        }

        DivisorHorizontal(
            modifier = Modifier.padding(
                top = AppTheme.dimensions.paddingSpaceBetweenComponentsSmall,
            )
        )
    }
}

@Composable
private fun CommentComponent(commentsBean: CommentsBean) {
    Text(
        text = commentsBean.content.orEmpty(),
        modifier = Modifier
            .padding(
                start = AppTheme.dimensions.paddingSpaceBetweenComponentsSmall,
                bottom = AppTheme.dimensions.baseTweet.paddingBottomComment,
            )
    )
}

@Composable
private fun MomentHeadComponent() {
    ConstraintLayout(
        modifier = Modifier.fillMaxWidth(),
    ) {

        val (userProfile, userAvatar, userNickname) = createRefs()

        SubcomposeAsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(userProfile) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
                .defaultMinSize(minHeight = AppTheme.dimensions.baseTweet.userProfileMinHeight),
            model = "",
            loading = {
                Box(
                    modifier = Modifier
                        .size(AppTheme.dimensions.baseTweet.avatarSize)
                        .background(Color.LightGray)
                )
            },
            contentDescription = stringResource(R.string.content_description_user_profile_image)
        )

        SubcomposeAsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(userAvatar) {
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                }
                .size(AppTheme.dimensions.baseTweet.userAvatarSize)
                .padding(AppTheme.dimensions.paddingSpaceBetweenComponentsSmallX),
            model = "",
            loading = {
                Box(
                    modifier = Modifier
                        .size(AppTheme.dimensions.baseTweet.avatarSize)
                        .background(Color.LightGray)
                )
            },
            contentDescription = stringResource(R.string.content_description_user_profile_image)
        )

        Text(
            text = "",
            modifier = Modifier
                .constrainAs(userNickname) {
                    bottom.linkTo(parent.bottom)
                    end.linkTo(userAvatar.start)
                }
                .padding(
                    end = AppTheme.dimensions.paddingSpaceBetweenComponentsSmall,
                    bottom = AppTheme.dimensions.baseTweet.paddingBottomUserNickname,
                ),
        )
    }
}

//@Preview
//@Composable
//fun MainScreen_Preview() {
//
//}