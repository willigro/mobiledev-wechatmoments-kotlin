package com.tws.moments.designsystem.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
fun emptyMutableString() = remember {
    mutableStateOf("")
}