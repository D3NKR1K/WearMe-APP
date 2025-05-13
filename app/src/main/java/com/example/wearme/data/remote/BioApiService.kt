package com.example.wearme.data.remote

import com.example.wearme.domain.model.api.Profile
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface BioApiService {
    @POST("/profile/enter/")
    fun humanization(@Body profile: Profile): Call<Void>

    @PUT("/profile/update/")
    fun rehumanization(@Body profile: Profile): Call<Void>

    @GET("/profile/get/")
    fun dehumanization(): Call<Profile>
}
