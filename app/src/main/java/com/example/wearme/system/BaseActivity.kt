package com.example.wearme.system

import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity: AppCompatActivity() {

    fun hideKeyboard() {
        Log.d("BaseActivity", "Hiding keyboard")
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    fun setLoading(enabled: Boolean, buttonId: Int) {
        findViewById<View>(buttonId)?.isEnabled = !enabled
        Log.d("BaseActivity", "setLoading: isLoading=$enabled, buttonId=$buttonId")
    }
}
