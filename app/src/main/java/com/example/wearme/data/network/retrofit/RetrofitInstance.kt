package com.example.wearme.data.network.retrofit

import com.example.wearme.data.remote.*
import com.example.wearme.domain.model.AuthInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val BASE_URL = "http://79.174.82.23:8000"

    private lateinit var authRetrofit: Retrofit
    private lateinit var noAuthRetrofit: Retrofit

    fun initWithoutToken() {
        noAuthRetrofit = Retrofit.Builder().baseUrl(BASE_URL).client(OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create()).build()
    }

    fun initWithToken(token: String) {
        authRetrofit = Retrofit.Builder().baseUrl(BASE_URL)
            .client(OkHttpClient.Builder().addInterceptor(AuthInterceptor(token)).build())
            .addConverterFactory(GsonConverterFactory.create()).build()
    }

    val userApiService: UserApiService by lazy { noAuthRetrofit.create(UserApiService::class.java) }

    val systemApiService: SystemApiService by lazy { authRetrofit.create(SystemApiService::class.java) }
    val bioApiService: BioApiService by lazy { authRetrofit.create(BioApiService::class.java) }
    val measurementsApiService: MeasurementsApiService by lazy {
        authRetrofit.create(
            MeasurementsApiService::class.java
        )
    }
    val clothesApiService: ClothesApiService by lazy { authRetrofit.create(clothesApiService::class.java) }
}