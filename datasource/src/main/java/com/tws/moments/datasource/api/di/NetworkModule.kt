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


private const val NETWORK_CACHE_SECONDS = 60
private const val OFFLINE_CACHE_MINUTES = 15
private const val CACHE_SIZE = (5 * 1024 * 1024).toLong()

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Named("online-cache-interceptor")
    @Provides
    fun provideOnlineCacheInterceptor(): Interceptor {
        return Interceptor { chain ->
            val request = chain.proceed(chain.request())

            val cacheControl = CacheControl.Builder()
                .maxAge(NETWORK_CACHE_SECONDS, TimeUnit.SECONDS)
                .build()

            request
                .newBuilder()
                .header("Cache-Control", cacheControl.toString()).build()
        }
    }

    @Named("offline-cache-interceptor")
    @Provides
    fun provideOfflineCacheInterceptor(
        @ApplicationContext context: Context,
    ): Interceptor {
        return Interceptor { chain ->
            var request = chain.request()

            if (hasNetwork(context) != true) {
                val cacheControl = CacheControl.Builder()
                    .maxStale(OFFLINE_CACHE_MINUTES, TimeUnit.MINUTES)
                    .onlyIfCached()
                    .build()

                request = request
                    .newBuilder()
                    .header("Cache-Control", cacheControl.toString())
                    .build()
            }

            chain.proceed(request)
        }
    }

    @Provides
    fun provesOkHttpClient(
        @ApplicationContext context: Context,
        @Named("online-cache-interceptor") onlineCacheInterceptor: Interceptor,
        @Named("offline-cache-interceptor") offlineCacheInterceptor: Interceptor,
    ): OkHttpClient {
        val myCache = Cache(context.cacheDir, CACHE_SIZE)

        return OkHttpClient.Builder()
            .cache(myCache)
            .addNetworkInterceptor(onlineCacheInterceptor)
            .addInterceptor(offlineCacheInterceptor)
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