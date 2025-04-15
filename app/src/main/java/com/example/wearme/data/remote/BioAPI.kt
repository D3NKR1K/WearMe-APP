package com.example.wearme.data.remote

import com.example.wearme.data.model.DehumanizationResponse
import com.example.wearme.data.model.HumanizationResponse
import com.example.wearme.domain.model.Bio
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT

interface BioAPI {
  @PUT("/bios/enter/")
  fun humanization(bio: Bio, @Header("Authorization") token: String): Call<HumanizationResponse>

  @GET("/bios/get/")
  fun dehumanization(@Header("Authorization") token: String): Call<DehumanizationResponse>
}
