package com.example.wearme.domain.model.api

import com.google.gson.annotations.SerializedName

enum class Gender { M, F, U }

data class Profile(
    @SerializedName("user_name") val name: String,
    @SerializedName("age") val age: Int,
    @SerializedName("gender") val gender: Gender
) {
    fun isUserNameValid() = name.length in 1 .. 30
}