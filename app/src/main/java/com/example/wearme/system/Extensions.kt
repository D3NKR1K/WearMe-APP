package com.example.wearme.system

import com.google.android.material.textfield.TextInputEditText

// Extensions.kt
fun TextInputEditText.getTText() = text.toString()
fun TextInputEditText.getInt() = text.toString().trim().toInt()
fun TextInputEditText.getDouble() = text.toString().trim().toDouble()
