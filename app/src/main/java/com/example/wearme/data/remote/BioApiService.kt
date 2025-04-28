package com.example.wearme.data.remote

import com.example.wearme.domain.model.api.Profile
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface BioApiService {
    @POST("/bios/enter/")
    fun humanization(@Body profile: Profile): Call<Void>

    @GET("/bios/get/")
    fun dehumanization(): Call<Profile>
}
