package com.example.wearme.data.remote

import com.example.wearme.domain.model.AuthInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val BASE_URL = "http://79.174.82.23:8000"

    private lateinit var authRetrofit: Retrofit
    private lateinit var noAuthRetrofit: Retrofit

    fun init(token: String) {
        authRetrofit = Retrofit.Builder().baseUrl(BASE_URL)
            .client(OkHttpClient.Builder().addInterceptor(AuthInterceptor(token)).build())
            .addConverterFactory(GsonConverterFactory.create()).build()

        noAuthRetrofit = Retrofit.Builder().baseUrl(BASE_URL).client(OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create()).build()
    }

    val userApi: UserApi by lazy { noAuthRetrofit.create(UserApi::class.java) }

    val serviceApi: ServiceApi by lazy { authRetrofit.create(ServiceApi::class.java) }
    val bioApi: BioAPI by lazy { authRetrofit.create(BioAPI::class.java) }
    val measurementsApi: MeasurementsApi by lazy { authRetrofit.create(MeasurementsApi::class.java) }
    val clothesApi: ClothesApi by lazy { authRetrofit.create(ClothesApi::class.java) }
}
