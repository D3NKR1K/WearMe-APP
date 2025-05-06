package com.example.wearme.data.remote

import com.example.wearme.domain.model.api.Cloth
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ClothesApiService {
    @GET("/clothes/")
    suspend fun getClothes(
        @Query("global_category_id") globalCategoryId: Int,
    ): Response<List<Cloth>>
}