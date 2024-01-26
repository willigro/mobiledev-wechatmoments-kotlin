package com.tws.moments

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.tws.moments.designsystem.theme.TwsMomentsTheme
import com.tws.moments.ui.main.MainScreenRoot
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TwsMomentsTheme {
                MainScreenRoot()
            }
        }
    }
}
