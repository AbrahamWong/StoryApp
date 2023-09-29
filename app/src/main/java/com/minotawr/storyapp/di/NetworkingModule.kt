package com.minotawr.storyapp.di

import com.google.gson.Gson
import com.minotawr.storyapp.StoryApp
import com.minotawr.storyapp.data.local.AuthLocalDataSource
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkingModule = module {
    single { provideGson() }
    single { provideConverterFactory(get()) }
    single { provideHttpLoggingInterceptor() }
    single { provideAuthInterceptor(get()) }

    single(named("api")) { provideRetrofit(get(), get(), get()) }
}

private fun provideGson() = Gson()

private fun provideConverterFactory(gson: Gson) =
    GsonConverterFactory.create(gson)

private fun provideHttpLoggingInterceptor() =
    HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

private fun provideAuthInterceptor(authLocalDataSource: AuthLocalDataSource) =
    Interceptor { chain ->
        val req = chain.request()
        val token = authLocalDataSource.getCached()?.loginResult?.token

        val requestHeader = req.newBuilder().apply {
            if (token != null)
                addHeader("Authorization", "Bearer $token")
        }.build()


        chain.proceed(requestHeader)
    }

private fun provideRetrofit(
    httpInterceptor: HttpLoggingInterceptor,
    authInterceptor: Interceptor,
    converterFactory: GsonConverterFactory,
): Retrofit =
    Retrofit.Builder()
        .baseUrl(StoryApp.baseUrl)
        .client(
            OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(httpInterceptor)
                .build()
        )
        .addConverterFactory(converterFactory)
        .build()