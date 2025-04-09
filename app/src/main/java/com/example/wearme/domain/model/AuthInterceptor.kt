package com.example.wearme.domain.model

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

// Интерцептор для добавления токена в запросы
class AuthInterceptor(private val token: String) : Interceptor {
  override fun intercept(chain: Interceptor.Chain): Response {
    val originalRequest: Request = chain.request()
    val newRequest: Request = originalRequest.newBuilder()
      .addHeader("Authorization", "Bearer $token")  // Добавляем токен в заголовок
      .build()
    return chain.proceed(newRequest)
  }
}
