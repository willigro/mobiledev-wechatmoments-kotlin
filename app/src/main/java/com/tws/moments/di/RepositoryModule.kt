package com.tws.moments.di

import com.tws.moments.api.MomentRepository
import com.tws.moments.api.MomentRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Singleton
    @Provides
    fun providesMomentRepository(): MomentRepository = MomentRepositoryImpl()
}