package com.example.wearme.domain.model

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

// Интерцептор для добавления токена в запросы
class AuthInterceptor(private val tokenProvider: () -> String?): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenProvider() ?: throw IOException("Missing authentication token")

        val request =
            chain.request().newBuilder().addHeader("Authorization", "Bearer $token").build()

        return chain.proceed(request)
    }
}
