package com.tws.moments.datasource.utils.di

import com.tws.moments.datasource.utils.AppClock
import com.tws.moments.datasource.utils.DateUtils
import com.tws.moments.datasource.utils.DateUtilsImpl
import com.tws.moments.datasource.utils.RealAppClock
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UtilsModule {

    @Singleton
    @Provides
    fun providesDateUtilsModule(
        appClock: AppClock,
    ): DateUtils = DateUtilsImpl(appClock)

    @Singleton
    @Provides
    fun providesAppClockModule(): AppClock = RealAppClock()
}