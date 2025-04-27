package com.example.wearme.domain.model.api

import com.google.gson.annotations.SerializedName

data class Measurements(
    @SerializedName("chest") val chest: Int,
    @SerializedName("waist") val waist: Int,
    @SerializedName("hips") val hips: Int,
    @SerializedName("foot") val foot: Double
)