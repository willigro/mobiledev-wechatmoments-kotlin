package com.tws.moments.ui.main

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.ClickableText
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
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
import com.tws.moments.designsystem.theme.RoundedCornerShapeSmall
import com.tws.moments.viewmodels.MainEvent
import com.tws.moments.viewmodels.MainUiState
import com.tws.moments.viewmodels.MainViewModel
import kotlinx.coroutines.launch

private const val TAG = "MainScreen##"
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

                // Is not loading more data and it is the last item
                if(uiState.isFetchingMore.not() && index == (uiState.tweets?.size ?: 0) - 1) {
                    Log.i(TAG, "Loading more")

                    onEvent(
                        MainEvent.FetchMoreTweets
                    )
                }
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
        Column(modifier = Modifier.fillMaxWidth()) {
            MomentHeaderComponent(userBean = userBean)
            MomentItemComponent(tweetBean = tweetBean)
        }
    } else {
        MomentItemComponent(tweetBean = tweetBean)
    }

}

@Composable
private fun MomentItemComponent(
    tweetBean: TweetBean,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = AppTheme.dimensions.paddingSpaceBetweenComponentsSmall)
            .padding(horizontal = AppTheme.dimensions.paddingScreenDefault),
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
                            .background(Color.LightGray)
                    )
                },
                contentDescription = stringResource(R.string.content_description_sender_avatar_image)
            )

            Text(
                text = tweetBean.sender?.nick.orEmpty(),
                color = Color(0xFF4152C9), // TODO (rittmann) move to material
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
                        width = Dimension.fillToConstraints
                    }
                    .padding(
                        start = AppTheme.dimensions.paddingSpaceBetweenComponentsSmallX,
                    )
            ) {
                Text(
                    text = tweetBean.content.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF333333) // TODO (rittmann) move to material
                    ),
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
                    )
                    .background(
                        color = Color(0xFFF0F0F0), // TODO (rittmann) move to material
                        shape = RoundedCornerShapeSmall,
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
private fun TweetImages(images: List<ImagesBean>?) {
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
                                    .background(Color.LightGray) // TODO (rittmann) move to material
                            )
                        },
                        contentDescription = stringResource(R.string.content_description_tweet_picture_image)
                    )
                }

                4 -> {
                    filteredList.chunked(2).forEach { photos ->
                        Row(modifier = Modifier.fillMaxWidth()) {
                            photos.forEachIndexed { index, photo ->
                                GridImageComponent(
                                    modifier = Modifier
                                        .padding(
                                            start = if (index == 0) {
                                                AppTheme.dimensions.zero
                                            } else {
                                                AppTheme.dimensions.paddingSpaceBetweenComponentsSmall
                                            },
                                            bottom = AppTheme.dimensions.paddingSpaceBetweenComponentsSmall,
                                        )
                                        .size(
                                            AppTheme.dimensions.baseTweet.gridImageSize,
                                        ),
                                    photo = photo,
                                )
                            }
                        }
                    }
                }

                else -> {
                    filteredList.chunked(IMAGE_SPAN_COUNT).forEach { photos ->
                        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                            val maxWidth = maxWidth
                            Row(modifier = Modifier.fillMaxWidth()) {
                                photos.forEachIndexed { index, photo ->
                                    GridImageComponent(
                                        modifier = Modifier
                                            .padding(
                                                start = if (index == 0) {
                                                    AppTheme.dimensions.zero
                                                } else {
                                                    AppTheme.dimensions.paddingSpaceBetweenComponentsSmall
                                                },
                                                bottom = AppTheme.dimensions.paddingSpaceBetweenComponentsSmall,
                                            )
                                            .size(
                                                (maxWidth - (AppTheme.dimensions.paddingSpaceBetweenComponentsSmall * photos.size)) / IMAGE_SPAN_COUNT,
                                            ),
                                        photo = photo,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
}

@Composable
private fun GridImageComponent(modifier: Modifier, photo: String) {
    SubcomposeAsyncImage(
        modifier = modifier,
        contentScale = ContentScale.Crop,
        model = photo,
        loading = {
            Box(
                modifier = Modifier
                    .background(Color.LightGray) // TODO (rittmann) move to material
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
                        .background(Color.LightGray) // TODO (rittmann) move to material
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
                        .background(Color.LightGray) // TODO (rittmann) move to material
                )
            },
            contentDescription = stringResource(R.string.content_description_user_profile_image)
        )

        Text(
            text = userBean?.nick.orEmpty(),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Normal),
            modifier = Modifier
                .constrainAs(userNickname) {
                    bottom.linkTo(userProfile.bottom)
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
    val nick = commentsBean.sender?.nick.orEmpty()

    val annotatedString = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                color = Color(0xFF4152C9), // TODO (rittmann) move to material
                fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                letterSpacing = MaterialTheme.typography.bodyMedium.letterSpacing,
            )
        ) {
            pushStringAnnotation(nick, annotation = nick)
            append(nick)
        }

        append(": ${commentsBean.content.orEmpty()}")
    }

    ClickableText(
        modifier = Modifier
            .padding(
                start = AppTheme.dimensions.paddingSpaceBetweenComponentsSmall,
                bottom = AppTheme.dimensions.baseTweet.paddingBottomComment,
            ),
        text = annotatedString,
    ) { offset ->
        annotatedString.getStringAnnotations(offset, offset).firstOrNull()?.let {
            // TODO (rittmann) add smt like an animation to react to the click
            Log.i(TAG, "Clicked")
        }
    }
}


//@Preview
//@Composable
//fun MainScreen_Preview() {
//
//}