package com.example.wearme.data.remote

import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.POST

interface ServiceApi {
  @POST("/users/token/")
  fun checkToken(@Header("Authorization") token: String): Call<Void>
}