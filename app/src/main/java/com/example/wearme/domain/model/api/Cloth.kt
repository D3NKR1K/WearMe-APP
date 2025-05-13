package com.example.wearme.domain.model.api

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

enum class ClothingGenderEnum { M, F, U }


@Parcelize
data class Cloth(
    @SerializedName("global_category_id") val globalCategory: Int,
    @SerializedName("sub_category_id") val category: Int?,
    @SerializedName("color_id") val color: Int?,

    @SerializedName("name") val name: String,
    @SerializedName("gender") val gender: ClothingGenderEnum,
    @SerializedName("stars") val stars: Float,
    @SerializedName("comments") val comments: Int,
    @SerializedName("article") val article: Int,
    @SerializedName("photo_url") val photoUrl: String,
    @SerializedName("variation") val variation: Variation,
): Parcelable

@Parcelize
data class Variation(
    val chest: Int?,
    val waist: Int?,
    val hips: Int?,
    val foot: Float?,
    @SerializedName("match_score") val matchScore: Int,
    @SerializedName("size_id") val sizeId: Int
): Parcelable