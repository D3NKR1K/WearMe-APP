package com.example.wearme.data.remote

import com.example.wearme.data.model.MessageResponse
import com.example.wearme.domain.model.FootMeasurements
import com.example.wearme.domain.model.UnderMeasurements
import com.example.wearme.domain.model.UpperMeasurements
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT

interface MeasurementsApi {
    @PUT("/body_measurements/upper/enter/")
    fun updateUpper(
        @Body upperMeasurements: UpperMeasurements, @Header("Authorization") token: String
    ): Call<MessageResponse>

    @PUT("/body_measurements/under/enter/")
    fun updateUnder(
        @Body underMeasurements: UnderMeasurements, @Header("Authorization") token: String
    ): Call<MessageResponse>

    @PUT("/body_measurements/foot/enter/")
    fun updateFoot(
        @Body footMeasurements: FootMeasurements, @Header("Authorization") token: String
    ): Call<MessageResponse>

    @GET("/body_measurements/upper/get/")
    fun getUpper(
        @Header("Authorization") token: String
    ): Call<UpperMeasurements>

    @GET("/body_measurements/under/get/")
    fun getUnder(
        @Header("Authorization") token: String
    ): Call<UnderMeasurements>

    @GET("/body_measurements/foot/get/")
    fun getFoot(
        @Header("Authorization") token: String
    ): Call<FootMeasurements>
}