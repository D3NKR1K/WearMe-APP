package com.example.wearme.data.network.api

import android.content.Intent
import android.util.Log
import com.example.wearme.data.model.MeasurementsResponse
import com.example.wearme.ui.bio.MeasurementsActivity
import com.example.wearme.ui.home.ProductsActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MeasurementsCallback(private val activity: MeasurementsActivity):
  Callback<MeasurementsResponse> {
  override fun onResponse(
    call: Call<MeasurementsResponse>, response: Response<MeasurementsResponse>
  ) {
    when (response.code()) {
      204 -> {
        Log.i("[MEASUREMENTS UPDATE]", "Measurements were updated")
        activity.startActivity(Intent(activity, ProductsActivity::class.java))
        activity.finish()
      }

      422 -> {
        Log.e("[MEASUREMENTS UPDATE]", "Measurements were not updated")
      }
    }
  }

  override fun onFailure(call: Call<MeasurementsResponse?>, t: Throwable) {
    TODO("Not yet implemented")
  }
}