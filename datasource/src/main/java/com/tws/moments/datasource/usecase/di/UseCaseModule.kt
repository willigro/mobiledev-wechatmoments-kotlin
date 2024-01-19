package com.tws.moments.datasource.usecase.di

import com.tws.moments.datasource.repository.MomentRepository
import com.tws.moments.datasource.usecase.MomentsUseCase
import com.tws.moments.datasource.usecase.MomentsUseCaseImpl
import com.tws.moments.datasource.usecase.helpers.IDispatcher
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
        iDispatcher: IDispatcher,
        repository: MomentRepository
    ): MomentsUseCase = MomentsUseCaseImpl(iDispatcher, repository)
}