package com.example.wearme.data.remote

import com.example.wearme.data.model.MessageResponse
import com.example.wearme.domain.model.FootMeasurements
import com.example.wearme.domain.model.UnderMeasurements
import com.example.wearme.domain.model.UpperMeasurements
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

interface MeasurementsApiService {
    @PUT("/body_measurements/upper/")
    fun updateUpper(@Body upperMeasurements: UpperMeasurements): Call<MessageResponse>

    @PUT("/body_measurements/under/")
    fun updateUnder(@Body underMeasurements: UnderMeasurements): Call<MessageResponse>

    @PUT("/body_measurements/foot/")
    fun updateFoot(
        @Body footMeasurements: FootMeasurements
    ): Call<MessageResponse>

    @GET("/body_measurements/upper/")
    fun getUpper(): Call<UpperMeasurements>

    @GET("/body_measurements/under/")
    fun getUnder(): Call<UnderMeasurements>

    @GET("/body_measurements/foot/")
    fun getFoot(): Call<FootMeasurements>
}