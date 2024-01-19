package com.tws.moments.datasource.usecase.helpers.di

import com.tws.moments.datasource.usecase.helpers.CoroutineDispatcherHelper
import com.tws.moments.datasource.usecase.helpers.IDispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoroutineDispatcherModule {

    @Singleton
    @Provides
    fun provideCoroutineDispatcher(): IDispatcher {
        return CoroutineDispatcherHelper()
    }
}