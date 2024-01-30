package com.tws.moments.ui.main

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.tws.moments.R
import com.tws.moments.datasource.api.entry.CommentsBean
import com.tws.moments.datasource.api.entry.ImagesBean
import com.tws.moments.datasource.api.entry.UserBean
import com.tws.moments.datasource.shared.data.TweetBean
import com.tws.moments.designsystem.components.DivisorHorizontal
import com.tws.moments.designsystem.components.ErrorImageComponent
import com.tws.moments.designsystem.components.LoadingImageComponent
import com.tws.moments.designsystem.theme.AppTheme
import com.tws.moments.designsystem.theme.RoundedCornerShapeSmall
import kotlinx.coroutines.launch

private const val TAG = "MainScreen##"
private const val IMAGE_SPAN_COUNT = 3

private const val ONE_PICTURE = 1
private const val FOUR_PICTURES = 4

private const val ANIMATION_VISIBILITY_DURATION = 150
private const val ANIMATION_VISIBILITY_DELAY = 150

// TODO move these colors to the materials
//  names aren't represeting the right color, but an aproximation
val blue = Color(0xFF4152C9)
val red = Color(0xFFC21149)
val white = Color(0xFFF0F0F0)

@Composable
fun MainScreenRoot(
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

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

    val toolbarHeight = remember {
        mutableStateOf(0.dp)
    }

    val lazyListState = rememberLazyListState()
    val directionalLazyListState = rememberDirectionalLazyListState(
        lazyListState
    )

    SwipeRefresh(
        modifier = Modifier.fillMaxSize(),
        state = swipeRefreshState,
        onRefresh = {
            coroutineScope.launch {
                onEvent(
                    MainEvent.RefreshTweets
                )
            }
        },
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    state = lazyListState,
                ) {
                    item {
                        MomentHeaderComponent(
                            toolbarHeight = toolbarHeight,
                            userBean = uiState.userBean,
                        )
                    }

                    if (uiState.hasErrorOnTweets.not()) {
                        itemsIndexed(
                            items = uiState.tweets.orEmpty(),
                            key = { _, tweet ->
                                tweet.id.toString()
                            }
                        ) { index, tweet ->
                            MomentItemComponent(
                                tweetBean = tweet,
                                onEvent = onEvent,
                            )

                            // Is not loading more data and it is the last item
                            if (uiState.isFetchingMore.not() && index == (uiState.tweets?.size
                                    ?: 0) - 1
                            ) {
                                onEvent(
                                    MainEvent.FetchMoreTweets
                                )
                            }
                        }
                    }
                }

                if (uiState.hasErrorOnTweets) {
                    TweetsHasNotBeenFoundComponent()
                }
            }

            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            ToolbarComponent(
                directionalLazyListState = directionalLazyListState,
                toolbarHeight = toolbarHeight,
            )
        }
    }
}

@Composable
fun TweetsHasNotBeenFoundComponent() {
    ConstraintLayout(
        modifier = Modifier.fillMaxSize(),
    ) {

        val (message, image) = createRefs()

        Text(
            modifier = Modifier
                .constrainAs(message) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(image.top)
                }
                .padding(bottom = AppTheme.dimensions.paddingSpaceBetweenComponentsMediumX),
            text = stringResource(id = R.string.message_tweets_has_not_been_found),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium,
        )

        val composition by rememberLottieComposition(LottieCompositionSpec.Asset("lottie_anim_empty_result.json"))
        LottieAnimation(
            modifier = Modifier.constrainAs(image) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            },
            composition = composition,
        )
    }
}

// TODO (rittmann) break into small components
@Composable
private fun MomentItemComponent(
    tweetBean: TweetBean,
    onEvent: (MainEvent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = AppTheme.dimensions.paddingSpaceBetweenComponentsSmallX)
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
                    LoadingImageComponent()
                },
                contentDescription = stringResource(R.string.content_description_sender_avatar_image)
            )

            Text(
                text = tweetBean.sender?.nick.orEmpty(),
                color = blue, // TODO (rittmann) move to material
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
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 5,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(
                        bottom = AppTheme.dimensions.paddingSpaceBetweenComponentsSmall
                    )
                )

                TweetImages(images = tweetBean.images)

                Text(
                    modifier = Modifier.padding(
                        top = AppTheme.dimensions.paddingSpaceBetweenComponentsSmall,
                        bottom = AppTheme.dimensions.paddingSpaceBetweenComponentsSmallX,
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    text = tweetBean.time.orEmpty(),
                )
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
                        color = white, // TODO (rittmann) move to material
                        shape = RoundedCornerShapeSmall,
                    ),
            ) {
                CommentListArea(tweetBean.comments)

                val showCommentArea = remember {
                    mutableStateOf(false)
                }

                if (showCommentArea.value) {
                    ConstraintLayout(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        val (input, button) = createRefs()

                        // TODO (rittmann) update the comment using the an Event? or keep it he?
                        val comment = remember { mutableStateOf("") }

                        CommentTextArea(
                            modifier = Modifier
                                .constrainAs(input) {
                                    top.linkTo(parent.top)
                                    start.linkTo(parent.start)
                                    end.linkTo(parent.end)
                                }
                                .fillMaxWidth(),
                            comment = comment,
                        )

                        Button(
                            modifier = Modifier.constrainAs(button) {
                                top.linkTo(input.bottom)
                                end.linkTo(parent.end)
                            },
                            onClick = {
                                onEvent(
                                    MainEvent.ShareNewComment(tweetBean, comment.value)
                                )

                                // TODO (rittmann) clear after successfully create the comment?
                                comment.value = ""
                            }
                        ) {
                            Text(text = "Comment")
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        ClickableText(
                            modifier = Modifier.padding(
                                AppTheme.dimensions.paddingSpaceBetweenComponentsSmall
                            ),
                            text = buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        color = red, // TODO (rittmann) move to material
                                        fontWeight = MaterialTheme.typography.bodySmall.fontWeight,
                                        fontSize = MaterialTheme.typography.bodySmall.fontSize,
                                        letterSpacing = MaterialTheme.typography.bodySmall.letterSpacing,
                                    )
                                ) {
                                    append("Share a comment")
                                }
                            }
                        ) {
                            showCommentArea.value = true
                        }
                    }
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
fun CommentListArea(comments: List<CommentsBean>?) {
    comments?.forEach { comment ->
        CommentComponent(commentsBean = comment)
    }
}

@Composable
fun CommentTextArea(
    modifier: Modifier,
    comment: MutableState<String>,
) {
    TextField(
        modifier = modifier,
        value = comment.value,
        onValueChange = { comment.value = it },
    )
}

@Composable
private fun TweetImages(images: List<ImagesBean>?) {
    // TODO it needs to be filtered from the VM or UseCase
    images
        ?.asSequence()
        ?.map { it.url ?: "" }
        ?.filter { it.isNotEmpty() }
        ?.toList()
        ?.also { filteredList ->
            when (filteredList.size) {
                ONE_PICTURE -> {
                    // TODO (rittmann) apply this calc to the size, see [SingleImageView]
                    // imageWidth = (measuredHeight * bm.width * 1f / bm.height).toInt()
                    SubcomposeAsyncImage(
                        modifier = Modifier
                            .size(
                                width = AppTheme.dimensions.baseTweet.singleImageWidth,
                                height = AppTheme.dimensions.baseTweet.singleImageHeight,
                            )
                            .padding(
                                bottom = AppTheme.dimensions.paddingSpaceBetweenComponentsSmall,
                            ),
                        contentScale = ContentScale.Crop,
                        model = filteredList[0],
                        loading = {
                            LoadingImageComponent()
                        },
                        contentDescription = stringResource(R.string.content_description_tweet_picture_image)
                    )
                }

                FOUR_PICTURES -> {
                    filteredList.chunked(size = 2).forEach { photos ->
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
            LoadingImageComponent()
        },
        contentDescription = stringResource(R.string.content_description_tweet_picture_image)
    )
}

@Composable
private fun MomentHeaderComponent(
    toolbarHeight: MutableState<Dp>,
    userBean: UserBean?,
) {
    ConstraintLayout(
        modifier = Modifier.fillMaxWidth(),
    ) {

        val (toolbarBox, userProfile, userAvatar, userNickname) = createRefs()

        Box(
            modifier = Modifier
                .constrainAs(toolbarBox) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
                .height(toolbarHeight.value)
                .background(MaterialTheme.colorScheme.surfaceVariant),
        )

        SubcomposeAsyncImage(
            modifier = Modifier
                .constrainAs(userProfile) {
                    top.linkTo(toolbarBox.bottom)
                    bottom.linkTo(parent.bottom)
                }
                .fillMaxWidth()
                .defaultMinSize(minHeight = AppTheme.dimensions.baseTweet.userProfileMinHeight)
                .padding(
                    bottom = AppTheme.dimensions.paddingSpaceBetweenComponentsMediumX,
                ),
            model = userBean?.profileImage,
            loading = {
                LoadingImageComponent()
            },
            error = {
                ErrorImageComponent()
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
                LoadingImageComponent()
            },
            error = {
                ErrorImageComponent(
                    modifier = Modifier
                        .border(BorderStroke(1.dp, white), CircleShape)
                        .clip(CircleShape)
                )
            },
            contentDescription = stringResource(R.string.content_description_user_profile_image)
        )

        Text(
            text = userBean?.nick.orEmpty(),
            style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Normal
            ),
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
                color = blue, // TODO (rittmann) move to material
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

@Composable
private fun ToolbarComponent(
    directionalLazyListState: DirectionalLazyListState,
    toolbarHeight: MutableState<Dp>,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current

    AnimatedVisibility(
        modifier = modifier
            .onGloballyPositioned {
                toolbarHeight.value = with(density) {
                    it.size.height.toDp()
                }
            },
        enter = slideInVertically(
            animationSpec = tween(
                durationMillis = ANIMATION_VISIBILITY_DURATION,
                delayMillis = ANIMATION_VISIBILITY_DELAY
            )
        ),
        exit = slideOutVertically(
            animationSpec = tween(
                durationMillis = ANIMATION_VISIBILITY_DURATION,
                delayMillis = ANIMATION_VISIBILITY_DELAY
            )
        ),
        visible = directionalLazyListState.showToolbar == ShowToolbar.Show
    ) {
        ConstraintLayout(
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = .8f)) // TODO (rittmann) Move alpha to a AppFloats?
                .padding(AppTheme.dimensions.paddingToolbar),
        ) {
            val (title, camera) = createRefs()

            Text(
                modifier = Modifier.constrainAs(title) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
                text = "Title",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.titleMedium,
            )

            IconButton(
                modifier = Modifier.constrainAs(camera) {
                    top.linkTo(title.top)
                    bottom.linkTo(title.bottom)
                    end.linkTo(parent.end)
                },
                onClick = { /*TODO*/ }
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_camera_alt_24),
                    contentDescription = stringResource(R.string.content_description_take_pictures),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun rememberDirectionalLazyListState(
    lazyListState: LazyListState,
): DirectionalLazyListState {
    return remember {
        DirectionalLazyListState(lazyListState)
    }
}

private enum class ShowToolbar {
    Show, Hide
}

private class DirectionalLazyListState(
    private val lazyListState: LazyListState
) {
    val showToolbar by derivedStateOf {
        if (lazyListState.isScrollInProgress.not()) {
            ShowToolbar.Show
        } else {
            if (lazyListState.firstVisibleItemIndex == 0) {
                ShowToolbar.Show
            } else {
                ShowToolbar.Hide
            }
        }
    }
}
