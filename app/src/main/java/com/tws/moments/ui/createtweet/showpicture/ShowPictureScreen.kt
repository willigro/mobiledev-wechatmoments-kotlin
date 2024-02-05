package com.tws.moments.ui.createtweet.showpicture

import androidx.camera.core.ExperimentalGetImage
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tws.moments.R
import com.tws.moments.core.file.BitmapExif
import com.tws.moments.designsystem.components.recomposeHighlighter
import com.tws.moments.designsystem.theme.AppTheme
import com.tws.moments.designsystem.theme.TwsMomentsTheme
import com.tws.moments.designsystem.theme.appTextFieldColors
import com.tws.moments.designsystem.utils.emptyMutableString
import com.tws.moments.ui.createtweet.shared.CreateTweetViewModel
import com.tws.moments.ui.createtweet.shared.ui.CreateTweetNavigationEvent
import com.tws.moments.ui.createtweet.shared.ui.CreateTweetToolbar
import com.tws.moments.ui.createtweet.shared.ui.CreateTweetUiEvent
import com.tws.moments.ui.createtweet.shared.ui.CreateTweetUiState

private val contentLabels = arrayOf(
    R.string.create_tweet_content_label_1,
    R.string.create_tweet_content_label_2,
    R.string.create_tweet_content_label_3,
)

@ExperimentalGetImage
@Composable
fun ShowPictureScreenRoot(
    viewModel: CreateTweetViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    ShowPictureScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onNavigationEvent = viewModel::onNavigationEvent,
    )
}

@Composable
private fun ShowPictureScreen(
    uiState: CreateTweetUiState,
    onEvent: (CreateTweetUiEvent) -> Unit,
    onNavigationEvent: (CreateTweetNavigationEvent) -> Unit,
) {
    val content = remember {
        mutableStateOf("")
    }

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (toolbar, image, footer) = createRefs()

        CreateTweetToolbar(
            modifier = Modifier.constrainAs(toolbar) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
            content = {
                Text(
                    text = stringResource(id = R.string.create_tweet_save_action),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (content.value.isEmpty()) {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .6f)
                    } else {
                        MaterialTheme.colorScheme.primary
                    },
                    modifier = Modifier
                        .clickable(
                            enabled = content.value.isNotEmpty()
                        ) {
                            onEvent(
                                CreateTweetUiEvent.SavePicture(content.value)
                            )
                        }
                        .padding(end = AppTheme.dimensions.paddingSpaceBetweenComponentsSmall),
                )
            },
        ) {
            onNavigationEvent(CreateTweetNavigationEvent.TakePicture)
        }

        ShowTakenImage(
            modifier = Modifier
                .constrainAs(image) {
                    top.linkTo(toolbar.bottom)
                    bottom.linkTo(footer.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    height = Dimension.fillToConstraints
                    width = Dimension.fillToConstraints
                },
            bitmapExif = uiState.bitmapExif,
        )

        ShowPictureFooter(
            modifier = Modifier
                .constrainAs(footer) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            content = content,
        )
    }
}

@Composable
fun ShowTakenImage(modifier: Modifier, bitmapExif: BitmapExif?) {
    if (bitmapExif?.bitmap == null) {
        //
    } else {
        Image(
            modifier = modifier.recomposeHighlighter(),
            bitmap = bitmapExif.bitmap!!.asImageBitmap(),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
        )
    }
}

@Composable
private fun ShowPictureFooter(
    modifier: Modifier,
    content: MutableState<String>,
) {
    val labelResourceId = remember {
        contentLabels.random()
    }

    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(AppTheme.dimensions.paddingSpaceBetweenComponentsMedium)
    ) {
        Text(
            text = stringResource(id = labelResourceId),
            style = MaterialTheme.typography.titleSmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
        )

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = AppTheme.dimensions.paddingSpaceBetweenComponentsSmallX
                )
                .recomposeHighlighter(),
            value = content.value,
            onValueChange = {
                content.value = it
            },
            colors = appTextFieldColors(),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ShowPictureFooter_Preview() {
    TwsMomentsTheme {
        ShowPictureFooter(
            modifier = Modifier.fillMaxSize(),
            content = emptyMutableString(),
        )
    }
}