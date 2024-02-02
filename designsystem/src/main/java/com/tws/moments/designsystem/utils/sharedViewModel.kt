package com.tws.moments.designsystem.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import com.tws.moments.designsystem.components.NavigationWrapper

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(navigationWrapper: NavigationWrapper): T {
    val navGraphRoute = destination.parent?.route ?: return hiltViewModel()
    val parentEntry = remember(this) {
        navigationWrapper.navController!!.getBackStackEntry(navGraphRoute)
    }

    return hiltViewModel(parentEntry)
}