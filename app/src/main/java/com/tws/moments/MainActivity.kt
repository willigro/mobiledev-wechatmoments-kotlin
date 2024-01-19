package com.tws.moments

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.tws.moments.designsystem.theme.TwsMomentsTheme
import com.tws.moments.designsystem.utils.ScreenAdaptiveUtil
import com.tws.moments.ui.main.MainScreenRoot
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "MainActivity##"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initWindow()

        ScreenAdaptiveUtil.adaptive(this)

        setContent {
            TwsMomentsTheme {
                MainScreenRoot()
            }
        }
    }

    private fun initWindow() {
        val flag = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.decorView.systemUiVisibility = flag
        window.statusBarColor = Color.TRANSPARENT
    }
}
