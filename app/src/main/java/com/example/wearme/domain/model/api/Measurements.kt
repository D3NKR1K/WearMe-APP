package com.example.wearme.domain.model.api

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Measurements(
    @SerializedName("chest") val chest: Int,
    @SerializedName("waist") val waist: Int,
    @SerializedName("hips") val hips: Int,
    @SerializedName("foot") val foot: Double
): Parcelable