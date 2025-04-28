package com.example.wearme.data.remote

import retrofit2.Call
import retrofit2.http.POST

interface SystemApiService {
    @POST("/system/validate-token/")
    fun checkToken(): Call<Void>
}