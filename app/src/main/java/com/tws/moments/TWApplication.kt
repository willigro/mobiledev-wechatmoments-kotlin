package com.tws.moments

import android.app.Application
import com.tws.moments.designsystem.utils.ScreenAdaptiveUtil
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TWApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ScreenAdaptiveUtil.adaptive(this)
    }
}
