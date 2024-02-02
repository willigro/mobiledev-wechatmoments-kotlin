package com.tws.moments.designsystem.components

import androidx.compose.runtime.Immutable
import androidx.navigation.NavController

@Immutable
data class NavigationWrapper(
    val navController: NavController? = null,
) {
    fun navigate(path: String) {
        navController?.navigate(path)
    }

    fun navigate(path: String, value: String) {
        navController?.navigate("$path/$value")
    }

    fun navigate(path: String, value: Any) {
        navController?.navigate("$path/$value")
    }

    fun pop() {
        navController?.navigateUp()
    }
}