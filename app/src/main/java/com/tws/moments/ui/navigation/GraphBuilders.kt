package com.tws.moments.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.tws.moments.designsystem.components.NavigationWrapper
import com.tws.moments.ui.createtweet.CreateTweetScreenRoot
import com.tws.moments.ui.main.MainScreenRoot

internal fun NavGraphBuilder.mainGraph(
    navigationWrapper: NavigationWrapper,
) {
    navigation(
        route = MainNavigation.Main.route,
        startDestination = MainNavigation.Main.destination,
    ) {
        composable(MainNavigation.Main.destination) {
            MainScreenRoot(navigationWrapper = navigationWrapper)
        }

        composable(MainNavigation.Create.destination) {
            CreateTweetScreenRoot(navigationWrapper = navigationWrapper)
        }
    }
}