package com.tws.moments.ui.navigation

import androidx.camera.core.ExperimentalGetImage
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.tws.moments.designsystem.utils.sharedViewModel
import com.tws.moments.ui.createtweet.take.single.TakeSinglePictureScreenRoot
import com.tws.moments.ui.createtweet.save.SaveTweetScreenRoot
import com.tws.moments.ui.createtweet.showpicture.ShowPictureScreenRoot
import com.tws.moments.ui.main.MainScreenRoot

internal fun NavGraphBuilder.mainGraph() {
    navigation(
        route = ScreensNavigation.Main.route,
        startDestination = ScreensNavigation.Main.destination,
    ) {
        composable(ScreensNavigation.Main.destination) {
            MainScreenRoot()
        }
    }
}

@ExperimentalGetImage
internal fun NavGraphBuilder.createGraph(navController: NavController) {
    navigation(
        route = ScreensNavigation.CreateTweet.TakeSinglePicture.route,
        startDestination = ScreensNavigation.CreateTweet.TakeSinglePicture.destination,
    ) {
        composable(ScreensNavigation.CreateTweet.TakeSinglePicture.destination) { navBackStack ->
            TakeSinglePictureScreenRoot(
                viewModel = navBackStack.sharedViewModel(navController)
            )
        }

        composable(ScreensNavigation.CreateTweet.ShowPictureTweet.destination) { navBackStack ->
            ShowPictureScreenRoot(
                viewModel = navBackStack.sharedViewModel(navController)
            )
        }

        composable(ScreensNavigation.CreateTweet.SaveTweet.destination) { navBackStack ->
            SaveTweetScreenRoot(
                viewModel = navBackStack.sharedViewModel(navController)
            )
        }
    }
}