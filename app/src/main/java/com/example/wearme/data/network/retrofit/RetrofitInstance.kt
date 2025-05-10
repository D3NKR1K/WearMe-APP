package com.example.wearme.data.network.retrofit

import com.example.wearme.data.remote.*
import com.example.wearme.domain.model.AuthInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//import timber.log.Timber

object RetrofitInstance {
    private const val BASE_URL = "http://79.174.82.23:8000"
    private const val AUTH_CLIENT_NULL_ERROR =
        "Auth client not initialized. Call initWithToken() first"
    private var tokenProvider: (() -> String?)? = null

    private val noAuthClient by lazy {
//        Timber.d("Initializing no-auth client")
        createRetrofitClient(OkHttpClient.Builder().build())
    }

    private var authClient: Retrofit? = null

    fun initWithToken(provider: () -> String?) {
        tokenProvider = provider
        authClient = createRetrofitClient(
            OkHttpClient.Builder().addInterceptor(AuthInterceptor(provider)).build()
        )
    }

    fun reinitializeAuthClient() {
        tokenProvider?.let {
            authClient = createRetrofitClient(
                OkHttpClient.Builder().addInterceptor(AuthInterceptor(it)).build()
            )
        }
    }

    fun clearAuthClient() {
//        Timber.d("Clearing auth client")
        authClient = null
    }

    val userApiService: UserApiService by lazy { noAuthClient.create(UserApiService::class.java) }

    val systemApiService: SystemApiService get() = getAuthService()
    val bioApiService: BioApiService get() = getAuthService()
    val measurementsApiService: MeasurementsApiService get() = getAuthService()
    val clothesApiService: ClothesApiService get() = getAuthService()

    private inline fun <reified T> getAuthService(): T {
        return authClient?.create(T::class.java) ?: throw IllegalStateException(
            AUTH_CLIENT_NULL_ERROR
        )
    }

    private fun createRetrofitClient(client: OkHttpClient): Retrofit {
        return Retrofit.Builder().baseUrl(BASE_URL).client(client)
            .addConverterFactory(GsonConverterFactory.create()).build()
    }
}