package com.example.wearme.data.remote

import com.example.wearme.data.model.ClothesNamesResponse
import com.example.wearme.domain.model.api.Cloth
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ClothesApiService {
    @GET("/clothes/")
    suspend fun getClothes(
        @Query("global_category_id") globalCategoryId: Int,
    ): Response<List<Cloth>>

    @POST("/clothes/names/")
    suspend fun getClothesData(
        @Body request: IdsRequest,
    ): Response<ClothesNamesResponse>
}

data class IdsRequest(
    @SerializedName("category_ids") val categoryIds: List<Int>,
    @SerializedName("color_ids") val colorIds: List<Int>
)
