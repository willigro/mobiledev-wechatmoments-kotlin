package com.tws.moments.ui.navigation

sealed class MainNavigation(val destination: String, val route: String = "main_route") {
    object Main : MainNavigation("main")
    object Create : MainNavigation("create")
}