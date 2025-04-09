package com.example.wearme.data.remote

import retrofit2.Response
import retrofit2.http.GET

interface ClothesApi {
  @GET("/static/images/")
  suspend fun getImagesUrls(): Response<List<String>>
}