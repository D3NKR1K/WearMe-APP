package com.example.wearme.data.network.api

import android.util.Log
import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TokenValidationCallback(private val liveData: MutableLiveData<Boolean>): Callback<Void> {

  override fun onResponse(call: Call<Void>, response: Response<Void>) {
    when (response.code()) {
      204 -> {
        Log.i("[TOKEN VALIDATION]", "Token is valid")
        liveData.postValue(true)
      }

      401 -> {
        Log.i("[TOKEN VALIDATION]", "Token is invalid")
        liveData.postValue(false)
      }

      else -> {
        Log.e("[TOKEN VALIDATION]", "Unexpected status code: ${response.code()}")
        liveData.postValue(false)
      }
    }
  }

  override fun onFailure(call: Call<Void>, t: Throwable) {
    Log.e("[TOKEN VALIDATION FAIL]", "Network error", t)
    liveData.postValue(false)
  }
}
