package com.tws.moments.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.tws.moments.designsystem.theme.AppTheme

@Composable
fun DivisorHorizontal(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(AppTheme.dimensions.dividerThickness)
            .background(Color(0xFFCDCDCD)) // TODO (rittmann) move it to material
    )
}