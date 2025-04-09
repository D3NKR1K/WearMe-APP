package com.example.wearme.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

  private const val BASE_URL = "http://79.174.82.23:8000"

  private val retrofit: Retrofit =
    Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()

  val authApi: AuthApi = retrofit.create(AuthApi::class.java)
  val tokenApi: ServiceApi = retrofit.create(ServiceApi::class.java)
}

