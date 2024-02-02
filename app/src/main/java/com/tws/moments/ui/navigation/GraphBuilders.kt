package com.tws.moments.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.tws.moments.designsystem.components.NavigationWrapper
import com.tws.moments.designsystem.utils.sharedViewModel
import com.tws.moments.ui.createtweet.take.single.TakeSinglePictureScreenRoot
import com.tws.moments.ui.createtweet.save.SaveTweetScreenRoot
import com.tws.moments.ui.main.MainScreenRoot

internal fun NavGraphBuilder.mainGraph(
    navigationWrapper: NavigationWrapper,
) {
    navigation(
        route = ScreensNavigation.Main.route,
        startDestination = ScreensNavigation.Main.destination,
    ) {
        composable(ScreensNavigation.Main.destination) {
            MainScreenRoot(navigationWrapper = navigationWrapper)
        }
    }
}

internal fun NavGraphBuilder.createGraph(
    navigationWrapper: NavigationWrapper,
) {
    navigation(
        route = ScreensNavigation.CreateTweet.TakeSinglePicture.route,
        startDestination = ScreensNavigation.CreateTweet.TakeSinglePicture.destination,
    ) {
        composable(ScreensNavigation.CreateTweet.TakeSinglePicture.destination) { navBackStack ->
            TakeSinglePictureScreenRoot(
                navigationWrapper = navigationWrapper,
                viewModel = navBackStack.sharedViewModel(navigationWrapper = navigationWrapper)
            )
        }

        composable(ScreensNavigation.CreateTweet.SaveTweet.destination) { navBackStack ->
            SaveTweetScreenRoot(
                navigationWrapper = navigationWrapper,
                viewModel = navBackStack.sharedViewModel(navigationWrapper = navigationWrapper)
            )
        }
    }
}