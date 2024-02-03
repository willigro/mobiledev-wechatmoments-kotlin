package com.tws.moments.core.file.di

import android.content.Context
import com.tws.moments.core.file.FileResolver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class FileResolverModule {

    @Provides
    @Singleton
    fun providesExecutorService(): ExecutorService = Executors.newSingleThreadExecutor()

    @Provides
    @Singleton
    fun providesFileResolver(
        @ApplicationContext context: Context,
        executorService: ExecutorService,
    ): FileResolver = FileResolver(context, executorService)
}