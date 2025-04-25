package com.example.wearme.data.remote

import com.example.wearme.data.model.MessageResponse
import com.example.wearme.domain.model.api.Profile
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT

interface BioAPI {
  @PUT("/bios/enter/")
  fun humanization(
    @Body profile: Profile, @Header("Authorization") token: String
  ): Call<MessageResponse>

  @GET("/bios/get/")
  fun dehumanization(@Header("Authorization") token: String): Call<MessageResponse>
}
