package com.tws.moments

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.tws.moments.core.tracker.track
import com.tws.moments.designsystem.theme.TwsMomentsTheme
import com.tws.moments.ui.navigation.AppNavigator
import com.tws.moments.ui.navigation.NavigationIntent
import com.tws.moments.ui.navigation.ScreensNavigation
import com.tws.moments.ui.navigation.createGraph
import com.tws.moments.ui.navigation.mainGraph
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var appNavigator: AppNavigator

    @OptIn(ExperimentalGetImage::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()

            NavigationEffects(
                navigationChannel = appNavigator.navigationChannel,
                navHostController = navController
            )

            TwsMomentsTheme {
                NavHost(
                    navController = navController,
                    startDestination = ScreensNavigation.Main.route,
                ) {
                    mainGraph()

                    createGraph(navController)
                }
            }
        }
    }
}

@Composable
fun NavigationEffects(
    navigationChannel: Channel<NavigationIntent>,
    navHostController: NavHostController
) {
    val activity = (LocalContext.current as? ComponentActivity)

    LaunchedEffect(activity, navHostController, navigationChannel) {
        navigationChannel.receiveAsFlow().collect { intent ->
            if (activity?.isFinishing == true) {
                return@collect
            }
            track(intent)
            when (intent) {
                is NavigationIntent.NavigateBack -> {
                    if (intent.route != null) {
                        navHostController.popBackStack(intent.route, intent.inclusive)
                    } else {
                        navHostController.popBackStack()
                    }
                }

                is NavigationIntent.NavigateTo -> {
                    navHostController.navigate(intent.route) {
                        launchSingleTop = intent.isSingleTop
                        if (intent.inclusive && intent.popUpToRoute == null) {
                            popUpTo(navHostController.graph.id) { inclusive = true }
                        } else {
                            intent.popUpToRoute?.let { popUpToRoute ->
                                popUpTo(popUpToRoute) { inclusive = intent.inclusive }
                            }
                        }
                    }
                }
            }
        }
    }
}
