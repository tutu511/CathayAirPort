package com.example.airport.request

import com.example.airport.request.api.ApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {

    private const val FLIGHT_API_BASE_URL = "https://www.kia.gov.tw/"
    private const val EXCHANGE_RATE_API_BASE_URL = "https://api.freecurrencyapi.com/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private fun createRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    val airPortApiService: ApiService by lazy {
        createRetrofit(FLIGHT_API_BASE_URL).create(ApiService::class.java)
    }

    val exchangeRateApiService: ApiService by lazy {
        createRetrofit(EXCHANGE_RATE_API_BASE_URL).create(ApiService::class.java)
    }

}