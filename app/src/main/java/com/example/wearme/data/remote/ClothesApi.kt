package com.example.wearme.data.remote

import com.example.wearme.domain.model.api.Cloth
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ClothesApi {
    @GET("/clothes/")
    suspend fun getClothes(
        @Query("global_category_id") globalCategoryId: Int,
        @Query("sub_category_id") subCategoryId: Int?,
        @Query("color") color: String?,
        @Header("Authorization") token: String
    ): Response<List<Cloth>>
}