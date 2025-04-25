package com.example.wearme.data.network.api

import android.content.Intent
import android.util.Log
import com.example.wearme.ui.auth.RedirectActivity
import com.example.wearme.ui.bio.BioActivity
import com.example.wearme.ui.home.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CheckMeasurementsCallback<T>(
    private val activity: RedirectActivity,
): Callback<T> {
    override fun onResponse(call: Call<T>, response: Response<T>) {
        when (response.code()) {
            200 -> {
                Log.i("[MEASUREMENTS GET]", "Data received")
                activity.startActivity(Intent(activity, MainActivity::class.java))
                activity.finish()
            }

            404 -> {
                Log.e("[MEASUREMENTS GET]", "Not found")
                activity.startActivity(Intent(activity, BioActivity::class.java))
                activity.finish()
            }
        }
    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        Log.e("[MEASUREMENTS GET]", "Network error", t)
    }
}