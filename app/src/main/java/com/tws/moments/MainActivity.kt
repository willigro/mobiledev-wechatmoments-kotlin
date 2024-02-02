package com.tws.moments

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.tws.moments.designsystem.components.NavigationWrapper
import com.tws.moments.designsystem.theme.TwsMomentsTheme
import com.tws.moments.ui.navigation.MainNavigation
import com.tws.moments.ui.navigation.mainGraph
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()

            val navigationWrapper = remember {
                NavigationWrapper(navController)
            }

            TwsMomentsTheme {
                NavHost(
                    navController = navController,
                    startDestination = MainNavigation.Main.route,
                ) {
                    mainGraph(navigationWrapper = navigationWrapper)
                }
            }
        }
    }
}
