package com.example.wearme.data.remote

import com.example.wearme.data.model.LoginResponse
import com.example.wearme.data.model.RegisterResponse
import com.example.wearme.domain.model.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApi {
  @POST("/users/register/")
  fun register(@Body user: User): Call<RegisterResponse>

  @POST("/users/login/")
  fun login(@Body user: User): Call<LoginResponse>
}
