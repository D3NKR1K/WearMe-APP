package com.example.wearme.domain.model.api

import com.google.gson.annotations.SerializedName

enum class ClothingGenderEnum { M, F, U }

data class Cloth(
    @SerializedName("global_category_id") val globalCategory: Int,
    @SerializedName("sub_category_id") val category: Int?,
    @SerializedName("name") val name: String,
    @SerializedName("color") val color: String,
    @SerializedName("gender") val gender: ClothingGenderEnum,
    @SerializedName("chest") val chest: Int?,
    @SerializedName("waist") val waist: Int?,
    @SerializedName("hips") val hips: Int?,
    @SerializedName("foot") val foot: Double?,
    @SerializedName("stars") val stars: Float,
    @SerializedName("comments") val comments: Int,
    @SerializedName("url") val url: String,
    @SerializedName("photo_url") val photoUrl: String
)