package com.example.wearme.data.network.api

import android.content.Intent
import android.util.Log
import com.example.wearme.ui.bio.MeasurementsEditActivity
import com.example.wearme.ui.home.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PutMeasurementsCallback(private val activity: MeasurementsEditActivity): Callback<Void> {
    override fun onResponse(
        call: Call<Void>, response: Response<Void>
    ) {
        when (response.code()) {
            204 -> {
                Log.i("[MEASUREMENTS UPDATE]", "Measurements were updated")
                activity.startActivity(Intent(activity, MainActivity::class.java))
                activity.finish()
            }

            422 -> {
                Log.e("[MEASUREMENTS UPDATE]", "Measurements were not updated")
            }

            else -> {
                Log.e("[MEASUREMENTS UPDATE]", "Unknown error")
            }
        }
    }

    override fun onFailure(call: Call<Void>, t: Throwable) {
        Log.e("[MEASUREMENTS UPDATE]", "onFailure")
    }
}