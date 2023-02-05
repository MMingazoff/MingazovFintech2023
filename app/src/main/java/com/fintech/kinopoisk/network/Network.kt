package com.fintech.kinopoisk.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object Network {
    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request();
            val request = original.newBuilder()
                .header("X-API-KEY", "895dca30-71e4-4ddf-b78a-7a798ef93e4e")
                .header("accept", "application/json")
                .method(original.method(), original.body())
                .build();
            chain.proceed(request);
        }
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://kinopoiskapiunofficial.tech/api/v2.2/films/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .client(httpClient)
        .build()

    val kinopoiskService: KinopoiskService by lazy {
        retrofit.create(KinopoiskService::class.java)
    }
}