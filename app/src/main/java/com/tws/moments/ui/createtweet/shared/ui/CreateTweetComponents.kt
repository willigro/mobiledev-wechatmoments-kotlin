package com.tws.moments.ui.createtweet.shared.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
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
import com.tws.moments.R
import com.tws.moments.designsystem.components.NavigationWrapper
import com.tws.moments.designsystem.theme.AppTheme

@Composable
fun CreateTweetToolbar(modifier: Modifier, navigationWrapper: NavigationWrapper) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant) // TODO (rittmann) Move alpha to a AppFloats?
            .padding(AppTheme.dimensions.paddingToolbar),
        horizontalArrangement = Arrangement.Start,
    ) {
        IconButton(
            onClick = {
                navigationWrapper.pop()
            }
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = stringResource(R.string.content_description_toolbar_arrow_back),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

    }
}