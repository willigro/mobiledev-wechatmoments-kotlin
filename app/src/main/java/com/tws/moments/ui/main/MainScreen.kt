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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.tws.moments.R
import com.tws.moments.api.entry.CommentsBean
import com.tws.moments.api.entry.ImagesBean
import com.tws.moments.api.entry.TweetBean
import com.tws.moments.api.entry.UserBean
import com.tws.moments.designsystem.components.DivisorHorizontal
import com.tws.moments.designsystem.theme.AppTheme
import com.tws.moments.viewmodels.MainEvent
import com.tws.moments.viewmodels.MainUiState
import com.tws.moments.viewmodels.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val IMAGE_SPAN_COUNT = 3

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
        // TODO (rittmann) can separate Item and Head of multiple ways, but I'm going to keep it for now
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(uiState.tweets.orEmpty()) { index, tweet ->
                BaseTweetComponent(
                    tweetBean = tweet,
                    isHead = index == 0,
                    userBean = uiState.userBean,
                )
            }
        }
    }
}

@Composable
private fun BaseTweetComponent(
    userBean: UserBean?,
    tweetBean: TweetBean,
    isHead: Boolean,
) {
    if (isHead) {
        MomentHeaderComponent(userBean = userBean)
    } else {
        MomentItemComponent(tweetBean = tweetBean)
    }

}

@Composable
fun MomentItemComponent(
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
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 5,
                    overflow = TextOverflow.Ellipsis,
                )

                TweetImages(images = tweetBean.images)
            }

            Column(
                modifier = Modifier
                    .constrainAs(comments) {
                        top.linkTo(tweetContent.bottom)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(tweetContent.start)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    }
                    .padding(
                        top = AppTheme.dimensions.paddingSpaceBetweenComponentsSmallX,
                    ),
            ) {
                tweetBean.comments?.forEach { comments ->
                    CommentComponent(commentsBean = comments)

                    DivisorHorizontal(
                        modifier = Modifier.padding(
                            top = AppTheme.dimensions.paddingSpaceBetweenComponentsSmall,
                        )
                    )
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
fun TweetImages(images: List<ImagesBean>?) {
    images
        ?.asSequence()
        ?.map { it.url ?: "" }
        ?.filter { it.isNotEmpty() }
        ?.toList()
        ?.also { filteredList ->
            when (filteredList.size) {
                1 -> {
                    // TODO (rittmann) apply this calc to the size, see [SingleImageView]
                    // imageWidth = (measuredHeight * bm.width * 1f / bm.height).toInt()
                    SubcomposeAsyncImage(
                        modifier = Modifier
                            .size(
                                width = AppTheme.dimensions.baseTweet.singleImageWidth,
                                height = AppTheme.dimensions.baseTweet.singleImageHeight,
                            )
                            .padding(
                                bottom = AppTheme.dimensions.paddingSpaceBetweenComponentsMediumX,
                            ),
                        contentScale = ContentScale.Crop,
                        model = filteredList[0],
                        loading = {
                            Box(
                                modifier = Modifier
                                    .size(AppTheme.dimensions.baseTweet.avatarSize)
                                    .background(Color.LightGray)
                            )
                        },
                        contentDescription = stringResource(R.string.content_description_tweet_picture_image)
                    )
                }

                4 -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2)
                    ) {
                        items(filteredList) { photo ->
                            GridImageComponent(photo)
                        }
                    }
                }

                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(IMAGE_SPAN_COUNT)
                    ) {
                        items(filteredList) { photo ->
                            GridImageComponent(photo)
                        }
                    }
                }
            }
        }
}

@Composable
fun GridImageComponent(photo: String) {
    SubcomposeAsyncImage(
        modifier = Modifier
            .size(
                AppTheme.dimensions.baseTweet.gridImageSize,
            )
            .padding(
                bottom = AppTheme.dimensions.paddingSpaceBetweenComponentsMediumX,
            ),
        contentScale = ContentScale.Crop,
        model = photo,
        loading = {
            Box(
                modifier = Modifier
                    .size(AppTheme.dimensions.baseTweet.avatarSize)
                    .background(Color.LightGray)
            )
        },
        contentDescription = stringResource(R.string.content_description_tweet_picture_image)
    )
}

@Composable
private fun MomentHeaderComponent(
    userBean: UserBean?,
) {
    ConstraintLayout(
        modifier = Modifier.fillMaxWidth(),
    ) {

        val (userProfile, userAvatar, userNickname) = createRefs()

        SubcomposeAsyncImage(
            modifier = Modifier
                .constrainAs(userProfile) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
                .fillMaxWidth()
                .defaultMinSize(minHeight = AppTheme.dimensions.baseTweet.userProfileMinHeight)
                .padding(
                    bottom = AppTheme.dimensions.paddingSpaceBetweenComponentsMediumX,
                ),
            model = userBean?.profileImage,
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
                .constrainAs(userAvatar) {
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                }
                .size(AppTheme.dimensions.baseTweet.userAvatarSize)
                .padding(
                    end = AppTheme.dimensions.paddingSpaceBetweenComponentsSmallX
                ),
            model = userBean?.avatar,
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
            text = userBean?.nick.orEmpty(),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Normal),
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

@Composable
private fun CommentComponent(commentsBean: CommentsBean) {
    Text(
        text = commentsBean.content.orEmpty(),
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier
            .padding(
                start = AppTheme.dimensions.paddingSpaceBetweenComponentsSmall,
                bottom = AppTheme.dimensions.baseTweet.paddingBottomComment,
            ),
    )
}


//@Preview
//@Composable
//fun MainScreen_Preview() {
//
//}