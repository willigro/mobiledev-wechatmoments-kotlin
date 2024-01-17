package com.tws.moments.repository.di

import com.tws.moments.api.MomentService
import com.tws.moments.repository.MomentRepository
import com.tws.moments.repository.MomentRepositoryImpl
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
    fun providesMomentRepository(
        momentService: MomentService
    ): MomentRepository = MomentRepositoryImpl(momentService)
}