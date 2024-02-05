package com.tws.moments.ui.createtweet.shared.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.constraintlayout.compose.ConstraintLayout
import com.tws.moments.R
import com.tws.moments.designsystem.theme.AppTheme

@Composable
fun CreateTweetToolbar(
    modifier: Modifier,
    content: @Composable () -> Unit = {},
    onBack: () -> Unit,
) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant) // TODO (rittmann) Move alpha to a AppFloats?
            .padding(AppTheme.dimensions.paddingToolbar),
    ) {
        val (startContent, endContent) = createRefs()

        IconButton(
            modifier = Modifier.constrainAs(startContent) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
            },
            onClick = onBack,
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = stringResource(R.string.content_description_toolbar_arrow_back),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Box(
            modifier = Modifier.constrainAs(endContent) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                end.linkTo(parent.end)
            }
        ) {
            content()
        }
    }

    BackHandler {
        onBack()
    }
}