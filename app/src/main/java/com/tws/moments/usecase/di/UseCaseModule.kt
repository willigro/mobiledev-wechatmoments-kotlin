package com.tws.moments.usecase.di

import com.tws.moments.repository.MomentRepository
import com.tws.moments.usecase.MomentsUseCase
import com.tws.moments.usecase.MomentsUseCaseImpl
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