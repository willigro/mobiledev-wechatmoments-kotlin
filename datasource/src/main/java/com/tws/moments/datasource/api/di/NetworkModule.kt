package com.tws.moments.datasource.api.di

import android.content.Context
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.tws.moments.datasource.api.MomentService
import com.tws.moments.datasource.api.utils.hasNetwork
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Named("cache-interceptor")
    @Provides
    fun provideCacheInterceptor(
        @ApplicationContext context: Context,
    ): Interceptor {
        return Interceptor { chain ->
            val request = chain.proceed(chain.request())

            if (hasNetwork(context) == true) {
                val cacheControl = CacheControl.Builder()
                    .maxAge(60, TimeUnit.SECONDS)
                    .build()

                request
                    .newBuilder()
                    .header("Cache-Control", cacheControl.toString()).build()
            } else {
                val cacheControl = CacheControl.Builder()
                    .maxStale(15, TimeUnit.MINUTES)
                    .onlyIfCached()
                    .build()

                request
                    .newBuilder()
                    .header(
                        "Cache-Control",
                        cacheControl.toString()
                    ).build()
            }
        }
    }

    @Provides
    fun provesOkHttpClient(
        @ApplicationContext context: Context,
        @Named("cache-interceptor") cacheInterceptor: Interceptor,
    ): OkHttpClient {
        val cacheSize = (5 * 1024 * 1024).toLong()
        val myCache = Cache(context.cacheDir, cacheSize)

        return OkHttpClient.Builder()
            .cache(myCache)
            .addInterceptor(cacheInterceptor)
            .build()
    }

    @Provides
    fun providesRetrofit(
        okHttpClient: OkHttpClient,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://192.168.1.67:2727/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(okHttpClient)
            .build()
    }

    @Provides
    fun providesMomentAPI(
        retrofit: Retrofit,
    ): MomentService {
        return retrofit.create(MomentService::class.java)
    }
}