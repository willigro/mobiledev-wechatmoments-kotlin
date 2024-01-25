package com.tws.moments

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TWApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
