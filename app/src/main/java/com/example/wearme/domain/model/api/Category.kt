package com.example.wearme.domain.model.api

import androidx.annotation.DrawableRes

data class Category(
    val name: String, @DrawableRes val iconResId: Int, val nameRes: String = ""
)
