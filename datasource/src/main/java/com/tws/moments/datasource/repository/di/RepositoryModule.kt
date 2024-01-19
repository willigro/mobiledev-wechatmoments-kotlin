package com.tws.moments.datasource.repository.di

import com.tws.moments.datasource.api.MomentService
import com.tws.moments.datasource.repository.MomentRepository
import com.tws.moments.datasource.repository.MomentRepositoryImpl
import com.tws.moments.datasource.usecase.helpers.IDispatcher
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
        iDispatcher: IDispatcher,
        momentService: MomentService,
    ): MomentRepository = MomentRepositoryImpl(iDispatcher, momentService)
}