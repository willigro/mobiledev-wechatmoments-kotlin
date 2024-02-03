package com.tws.moments.ui.navigation

sealed class ScreensNavigation(val destination: String, val route: String = "") {
    data object Main : ScreensNavigation("main", "main_route")
    sealed class CreateTweet : ScreensNavigation("create_route", "create_route_graph") {
        data object TakeSinglePicture : CreateTweet()
        data object SaveTweet : ScreensNavigation("save")
        data object ShowPictureTweet : ScreensNavigation("show")
    }
}