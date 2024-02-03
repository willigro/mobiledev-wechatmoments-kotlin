package com.tws.moments.ui.navigation.di

import com.tws.moments.ui.navigation.AppNavigator
import com.tws.moments.ui.navigation.AppNavigatorImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NavigationModule {

    @Provides
    @Singleton
    fun providesAppNavigator(): AppNavigator = AppNavigatorImpl()
}