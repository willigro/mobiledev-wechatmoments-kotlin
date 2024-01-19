package com.tws.moments.datasource.repository.di

import com.tws.moments.datasource.api.MomentService
import com.tws.moments.datasource.repository.MomentRepository
import com.tws.moments.datasource.repository.MomentRepositoryImpl
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