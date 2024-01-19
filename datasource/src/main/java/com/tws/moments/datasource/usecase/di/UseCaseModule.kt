package com.tws.moments.datasource.usecase.di

import com.tws.moments.datasource.repository.MomentRepository
import com.tws.moments.datasource.usecase.MomentsUseCase
import com.tws.moments.datasource.usecase.MomentsUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UseCaseModule {

    @Singleton
    @Provides
    fun providesUseCaseModule(
        repository: MomentRepository
    ): MomentsUseCase = MomentsUseCaseImpl(repository)
}