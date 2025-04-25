package com.example.wearme.domain.model.api

import com.google.gson.annotations.SerializedName

enum class ClothingGenderEnum { М, Ж }

data class Cloth(
  @SerializedName("global_category") val globalCategory: String,
  @SerializedName("category") val category: String,
  @SerializedName("name") val name: String,
  @SerializedName("color") val color: String,
  @SerializedName("gender") val gender: ClothingGenderEnum,
  @SerializedName("chest") val chest: Int?,
  @SerializedName("waist") val waist: Int?,
  @SerializedName("hips") val hips: Int?,
  @SerializedName("foot") val foot: Int?,
  @SerializedName("stars") val stars: Float,
  @SerializedName("comments") val comments: Int,
  @SerializedName("url") val url: String,
  @SerializedName("photo_url") val photoUrl: String
)