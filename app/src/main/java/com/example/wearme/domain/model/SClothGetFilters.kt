package com.example.wearme.domain.model

data class ClothGetFilters(
  val globalCategory: String, val category: String, val color: String? = null
)
