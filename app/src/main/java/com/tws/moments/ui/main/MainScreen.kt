package com.tws.moments.ui.main

import android.Manifest
import android.annotation.SuppressLint
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
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
import com.tws.moments.datasource.api.entry.SenderBean
import com.tws.moments.datasource.api.entry.UserBean
import com.tws.moments.datasource.shared.data.StableList
import com.tws.moments.datasource.shared.data.TweetBean
import com.tws.moments.datasource.shared.data.toStable
import com.tws.moments.designsystem.components.CollectSharedState
import com.tws.moments.designsystem.components.DivisorHorizontal
import com.tws.moments.designsystem.components.ErrorImageComponent
import com.tws.moments.designsystem.components.ExpandableText
import com.tws.moments.designsystem.components.ExpandableTextColumn
import com.tws.moments.designsystem.components.LoadingImageComponent
import com.tws.moments.designsystem.components.recomposeHighlighter
import com.tws.moments.designsystem.theme.AppTheme
import com.tws.moments.designsystem.theme.RoundedCornerShapeSmall
import com.tws.moments.designsystem.theme.TwsMomentsTheme
import com.tws.moments.designsystem.theme.appTextFieldColors
import com.tws.moments.designsystem.utils.registerLauncherSettings
import com.tws.moments.designsystem.utils.retrieveSettingsIntent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

private const val TAG = "MainScreen##"
private const val IMAGE_SPAN_COUNT = 3

private const val ONE_PICTURE = 1
private const val FOUR_PICTURES = 4

private const val ANIMATION_VISIBILITY_DURATION = 150
private const val ANIMATION_VISIBILITY_DELAY = 150

// TODO move these colors to the materials
//  names aren't represeting the right color, but an aproximation
val blue = Color(0xFF4152C9)

@Composable
fun MainScreenRoot(
    viewModel: MainViewModelCreateTweet = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val uiEvent = viewModel.uiEvent

    MainScreen(
        uiState = uiState,
        uiEvent = uiEvent,
        onEvent = viewModel::onEvent,
        onNavigationEvent = viewModel::onNavigationEvent,
    )
}

@Composable
private fun MainScreen(
    uiState: MainUiState,
    uiEvent: SharedFlow<List<String>?>,
    onEvent: (MainEvent) -> Unit,
    onNavigationEvent: (MainNavigationEvent) -> Unit,
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
        modifier = Modifier
            .fillMaxSize()
            .recomposeHighlighter(),
        state = swipeRefreshState,
        onRefresh = {
            coroutineScope.launch {
                onEvent(
                    MainEvent.RefreshTweets
                )
            }
        },
    ) {
        MainScreenListingComponent(
            lazyListState = lazyListState,
            directionalLazyListState = directionalLazyListState,
            uiState = uiState,
            toolbarHeight = toolbarHeight,
            onEvent = onEvent,
            onNavigationEvent = onNavigationEvent,
        )
    }

    SelectedImageComponent(
        uiEvent = uiEvent,
        onEvent = onEvent,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SelectedImageComponent(
    uiEvent: SharedFlow<List<String>?>,
    onEvent: (MainEvent) -> Unit,
) {
    val images = remember {
        mutableStateOf<List<String>?>(null)
    }

    if (images.value.isNullOrEmpty().not()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = .5f)
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (images.value?.size == 1) {
                SubcomposeAsyncImage(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            onEvent(
                                MainEvent.ClosesImage
                            )
                        }
                        .padding(AppTheme.dimensions.baseTweet.selectedImagePadding)
                        .recomposeHighlighter(),
                    model = images.value?.first(),
                    contentDescription = stringResource(R.string.content_description_post_image),
                )
            } else {
                val pagerState = rememberPagerState { images.value?.size ?: 0 }

                HorizontalPager(
                    state = pagerState,
                    contentPadding = PaddingValues(horizontal = 65.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            onEvent(
                                MainEvent.ClosesImage
                            )
                        }
                        .recomposeHighlighter(),
                ) { page ->

                    val pageOffset =
                        (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction

                    val scaleFactor = 0.75f + (1f - 0.75f) * (1f - pageOffset.absoluteValue)

                    SubcomposeAsyncImage(
                        modifier = Modifier
                            .graphicsLayer {
                                scaleX = scaleFactor
                                scaleY = scaleFactor
                            }
                            .alpha(
                                scaleFactor.coerceIn(0f, 1f)
                            )
                            .clickable {
                                onEvent(
                                    MainEvent.ClosesImage
                                )
                            }
                            .recomposeHighlighter(),
                        model = images.value?.get(page),
                        contentDescription = stringResource(R.string.content_description_post_images),
                    )
                }
            }
        }
    }

    CollectSharedState(sharedFlow = uiEvent) { url ->
        images.value = url
    }
}

@Composable
private fun MainScreenListingComponent(
    lazyListState: LazyListState,
    directionalLazyListState: DirectionalLazyListState,
    uiState: MainUiState,
    toolbarHeight: MutableState<Dp>,
    onEvent: (MainEvent) -> Unit,
    onNavigationEvent: (MainNavigationEvent) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .recomposeHighlighter()
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
            onNavigationEvent = onNavigationEvent,
        )
    }
}

@Composable
fun TweetsHasNotBeenFoundComponent() {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .recomposeHighlighter(),
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
            .padding(horizontal = AppTheme.dimensions.paddingScreenDefault)
            .recomposeHighlighter(),
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
                    .size(AppTheme.dimensions.baseTweet.avatarSize)
                    .clickable {
                        onEvent(
                            MainEvent.OpenImage(listOf(tweetBean.sender?.avatar.orEmpty()))
                        )
                    }
                    .recomposeHighlighter(),
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

            ContentAndImagesArea(
                modifier = Modifier
                    .constrainAs(tweetContent) {
                        top.linkTo(senderNickname.bottom)
                        start.linkTo(senderNickname.start)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    },
                tweetBean = tweetBean,
                onEvent = onEvent,
            )

            CommentsArea(
                modifier = Modifier
                    .constrainAs(comments) {
                        top.linkTo(tweetContent.bottom)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(tweetContent.start)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    },
                tweetBean = tweetBean,
                onEvent = onEvent,
            )
        }

        DivisorHorizontal(
            modifier = Modifier.padding(
                top = AppTheme.dimensions.paddingSpaceBetweenComponentsSmall,
            )
        )
    }
}

@Composable
fun CommentsArea(
    modifier: Modifier,
    tweetBean: TweetBean,
    onEvent: (MainEvent) -> Unit
) {
    Column(
        modifier = modifier
            .padding(
                top = AppTheme.dimensions.paddingSpaceBetweenComponentsSmallX,
            )
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShapeSmall,
            )
            .recomposeHighlighter(),
    ) {
        CommentListArea(tweetBean.comments)

        val showCommentArea = remember {
            mutableStateOf(false)
        }

        if (showCommentArea.value) {
            // TODO (rittmann) update the comment using the an Event? or keep it he?
            val comment = remember { mutableStateOf("") }

            CommentTextArea(
                tweetBean = tweetBean,
                comment = comment,
                onEvent = onEvent,
            )
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                IconButton(
                    modifier = Modifier.padding(
                        AppTheme.dimensions.paddingSpaceBetweenComponentsSmall
                    ),
                    onClick = {
                        showCommentArea.value = true
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_add_comment_24),
                        contentDescription = stringResource(R.string.content_description_type_comment),
                    )
                }
            }
        }
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
    tweetBean: TweetBean,
    comment: MutableState<String>,
    onEvent: (MainEvent) -> Unit,
) {
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(AppTheme.dimensions.paddingSpaceBetweenComponentsMedium)
            .recomposeHighlighter(),
        value = comment.value,
        onValueChange = { comment.value = it },
        colors = appTextFieldColors(),
        trailingIcon = {
            if (tweetBean.isSendingComment) {
                CircularProgressIndicator(
                    modifier = Modifier.size(AppTheme.dimensions.progressSizeSmall)
                )
            } else {
                IconButton(
                    enabled = comment.value.isNotEmpty(),
                    onClick = {
                        onEvent(
                            MainEvent.ShareNewComment(tweetBean, comment.value)
                        )

                        // TODO (rittmann) clear after successfully create the comment?
                        comment.value = ""
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = stringResource(id = R.string.content_description_send_comment),
                        tint = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }
        }
    )
}

@Composable
private fun CommentComponent(commentsBean: CommentsBean) {
    val nick = commentsBean.sender?.nick.orEmpty()

    val typography = MaterialTheme.typography

    val annotatedString = remember {
        buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    color = blue, // TODO (rittmann) move to material
                    fontWeight = typography.bodyMedium.fontWeight,
                    fontSize = typography.bodyMedium.fontSize,
                    letterSpacing = typography.bodyMedium.letterSpacing,
                )
            ) {
                pushStringAnnotation(nick, annotation = nick)
                append(nick)
            }

            append(": ${commentsBean.content.orEmpty()}")
        }
    }


    ExpandableTextColumn(
        modifier = Modifier
            .padding(
                start = AppTheme.dimensions.paddingSpaceBetweenComponentsSmall,
                bottom = AppTheme.dimensions.baseTweet.paddingBottomComment,
            )
            .recomposeHighlighter(),
        annotatedString = annotatedString,
        style = MaterialTheme.typography.bodyMedium,
        showMoreText = stringResource(id = R.string.expandable_text_show_more),
        showLessText = stringResource(id = R.string.expandable_text_show_less),
        showMoreStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
    ) { lastCharIndex ->
        buildAnnotatedString {
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

            // Display truncated text and "Show More" button when collapsed.
            val adjustText =
                commentsBean.content
                    .orEmpty()
                    .substring(startIndex = 0, endIndex = lastCharIndex)
                    .dropLastWhile { Character.isWhitespace(it) || it == '.' }

            append(": $adjustText")
        }
    }

//    ClickableText(
//        modifier = Modifier
//            .padding(
//                start = AppTheme.dimensions.paddingSpaceBetweenComponentsSmall,
//                bottom = AppTheme.dimensions.baseTweet.paddingBottomComment,
//            )
//            .recomposeHighlighter(),
//        text = annotatedString,
//    ) { offset ->
//        annotatedString.getStringAnnotations(offset, offset).firstOrNull()?.let {
//            // TODO (rittmann) add smt like an animation to react to the click
//            Log.i(TAG, "Clicked")
//        }
//    }
}

@Composable
fun ContentAndImagesArea(
    modifier: Modifier,
    tweetBean: TweetBean,
    onEvent: (MainEvent) -> Unit,
) {
    Column(
        modifier = modifier
            .padding(
                start = AppTheme.dimensions.paddingSpaceBetweenComponentsSmallX,
            )
            .recomposeHighlighter(),
    ) {
        ExpandableText(
            modifier = Modifier.padding(
                bottom = AppTheme.dimensions.paddingSpaceBetweenComponentsSmall
            ),
            text = tweetBean.content.orEmpty(),
            style = MaterialTheme.typography.bodyMedium,
            showMoreText = stringResource(id = R.string.expandable_text_show_more_ellipsis),
            showLessText = stringResource(id = R.string.expandable_text_show_less),
        )

        TweetImages(
            imageUrls = tweetBean.imagesUrls,
            onEvent = onEvent,
        )

        Text(
            modifier = Modifier.padding(
                top = AppTheme.dimensions.paddingSpaceBetweenComponentsSmall,
                bottom = AppTheme.dimensions.paddingSpaceBetweenComponentsSmallX,
            ),
            style = MaterialTheme.typography.labelMedium,
            text = tweetBean.time.orEmpty(),
        )
    }
}

@Composable
private fun TweetImages(
    imageUrls: StableList<String>?,
    onEvent: (MainEvent) -> Unit,
) {
    // TODO it needs to be filtered from the VM or UseCase
    imageUrls?.also {
        when (imageUrls.list.size) {
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
                        )
                        .clickable {
                            onEvent(
                                MainEvent.OpenImage(listOf(imageUrls.list[0]))
                            )
                        },
                    contentScale = ContentScale.Crop,
                    model = imageUrls.list[0],
                    loading = {
                        LoadingImageComponent()
                    },
                    contentDescription = stringResource(R.string.content_description_tweet_picture_image)
                )
            }

            FOUR_PICTURES -> {
                imageUrls.list.chunked(size = 2).forEach { photos ->
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
                                photos = imageUrls.list,
                                onEvent = onEvent,
                            )
                        }
                    }
                }
            }

            else -> {
                imageUrls.list.chunked(IMAGE_SPAN_COUNT).forEach { photos ->
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
                                    photos = imageUrls.list,
                                    onEvent = onEvent,
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
private fun GridImageComponent(
    modifier: Modifier,
    photo: String,
    photos: List<String>,
    onEvent: (MainEvent) -> Unit
) {
    SubcomposeAsyncImage(
        modifier = modifier
            .recomposeHighlighter()
            .clickable {
                onEvent(
                    MainEvent.OpenImage(photos)
                )
            },
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
        modifier = Modifier
            .fillMaxWidth()
            .recomposeHighlighter(),
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
                        .border(
                            BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.surfaceVariant,
                            ), CircleShape
                        )
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
private fun ToolbarComponent(
    directionalLazyListState: DirectionalLazyListState,
    toolbarHeight: MutableState<Dp>,
    modifier: Modifier = Modifier,
    onNavigationEvent: (MainNavigationEvent) -> Unit,
) {
    val componentActivity = (LocalContext.current as ComponentActivity)

    var shouldLaunchSettings = remember {
        false
    }

    val settingsLauncher = registerLauncherSettings(
        permissions = mutableListOf(Manifest.permission.CAMERA),
        componentActivity = componentActivity,
    ) {
        onNavigationEvent(MainNavigationEvent.OpenCreateTweet)
    }

    val activityResultLauncherCameraPermission =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                onNavigationEvent(MainNavigationEvent.OpenCreateTweet)
            } else {
                if (componentActivity.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
                        .not()
                ) {
                    shouldLaunchSettings = true
                }
            }
        }

    val density = LocalDensity.current

    AnimatedVisibility(
        modifier = modifier
            .onGloballyPositioned {
                toolbarHeight.value = with(density) {
                    it.size.height.toDp()
                }
            }
            .recomposeHighlighter(),
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
                onClick = {
                    if (shouldLaunchSettings) {
                        settingsLauncher.launch(
                            componentActivity.retrieveSettingsIntent()
                        )
                    } else {
                        activityResultLauncherCameraPermission.launch(
                            Manifest.permission.CAMERA
                        )
                    }
                }
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

@Preview(showBackground = true)
@Composable
fun Preview_CommentTextArea() {
    TwsMomentsTheme {
        CommentTextArea(
            tweetBean = TweetBean(),
            comment = remember {
                mutableStateOf("Testing text")
            }
        ) {}
    }
}

@Preview(showBackground = true)
@Composable
fun Preview_MainScreen_List_NotFound() {
    TwsMomentsTheme {
        MainScreen(
            uiState = MainUiState(
                hasErrorOnTweets = true,
            ),
            uiEvent = MutableSharedFlow(),
            onEvent = {},
        ) {

        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Preview(showBackground = true, heightDp = 1800)
@Composable
fun Preview_MainScreen_GridingTweets() {
    TwsMomentsTheme {
        MainScreen(
            uiState = MainUiState(
                userBean = UserBean(
                    username = "UserName",
                    nick = "nick",
                    avatar = "https://techops-recsys-lateral-hiring.github.io/moments-data/images/user/avatar/004.jpeg",
                    profileImage = "https://techops-recsys-lateral-hiring.github.io/moments-data/images/user/profile-image.jpeg"
                ),
                tweets = mutableStateListOf(
                    TweetBean(
                        content = "Content 1",
                        sender = SenderBean(nick = "Sender"),
                        imagesUrls = listOf("url 1").toStable(),
                    ),
                    TweetBean(
                        content = "Content 2",
                        sender = SenderBean(nick = "Sender"),
                        imagesUrls = listOf("url 1", "url 1", "url 1").toStable(),
                    ),
                    TweetBean(
                        content = "Content 3",
                        sender = SenderBean(nick = "Sender"),
                        imagesUrls = listOf("url 1", "url 1", "url 1", "url 1").toStable(),
                    ),
                    TweetBean(
                        content = "Content 4",
                        sender = SenderBean(nick = "Sender"),
                        imagesUrls = listOf(
                            "url 1", "url 1", "url 1", "url 1", "url 1", "url 1", "url 1", "url 1",
                            "url 1"
                        ).toStable(),
                    ),
                )
            ),
            uiEvent = MutableSharedFlow(),
            onEvent = {},
        ) {

        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Preview(showBackground = true)
@Composable
fun Preview_MainScreen_CommentedTweet() {
    TwsMomentsTheme {
        MainScreen(
            uiState = MainUiState(
                userBean = UserBean(
                    username = "UserName",
                    nick = "nick",
                    avatar = "https://techops-recsys-lateral-hiring.github.io/moments-data/images/user/avatar/004.jpeg",
                    profileImage = "https://techops-recsys-lateral-hiring.github.io/moments-data/images/user/profile-image.jpeg"
                ),
                tweets = mutableStateListOf(
                    TweetBean(
                        content = "Content 1",
                        sender = SenderBean(nick = "Sender"),
                        imagesUrls = listOf("url 1").toStable(),
                        comments = mutableStateListOf(
                            CommentsBean(
                                content = "Comment 1",
                                sender = SenderBean(
                                    nick = "nick",
                                )
                            ),
                            CommentsBean(
                                content = "Comment 2",
                                sender = SenderBean(
                                    nick = "nick 2",
                                )
                            ),
                        )
                    ),
                )
            ),
            uiEvent = MutableSharedFlow(),
            onEvent = {},
        ) {

        }
    }
}