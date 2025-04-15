package com.example.wearme.data.network.api

import android.util.Log
import com.example.wearme.ui.home.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GetMeasurementsCallback<T>(
  private val activity: MainActivity,
  private val onSuccess: (T?) -> Unit,
  private val onError: () -> Unit,
  private val validateValues: (T) -> Boolean
): Callback<T> {
  override fun onResponse(call: Call<T>, response: Response<T>) {
    when (response.code()) {
      200 -> {
        val body = response.body()
        if (body != null && validateValues(body)) {
          Log.i("[MEASUREMENTS GET]", "Data received")
          println(body.toString())
          onSuccess(body)
        } else {
          Log.e("[MEASUREMENTS GET]", "Empty response")
          onError()
        }
      }

      404 -> {
        Log.e("[MEASUREMENTS GET]", "Not found")
        onError()
      }

      else -> {
        Log.e("[MEASUREMENTS GET]", "Error: ${response.code()}")
        onError()
      }
    }
  }

  override fun onFailure(call: Call<T>, t: Throwable) {
    Log.e("[MEASUREMENTS GET]", "Network error", t)
    activity.runOnUiThread { activity.showNetworkErrorDialog() }
  }
}