package com.example.wearme.data.network.api

import android.util.Log
import com.example.wearme.domain.model.api.Profile
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CheckBioCallback(private val handler: BioCallbackHandler): Callback<Profile> {
    override fun onResponse(
        call: Call<Profile?>, response: Response<Profile?>
    ) {
        when (response.code()) {
            200 -> {
                Log.i("[BIO]", "BIO WAS FOUNDED")
                handler.navigateToMain()
            }

            404 -> {
                Log.i("[BIO]", "BIO WAS not FOUNDED")
                handler.navigateToBio()
            }

            401 -> {
                Log.i("[BIO]", "BIO WAS not FOUNDED")
                handler.navigateToBio()
            }

            else -> {
                Log.i("[BIO]", "Another response code: ${response.code()}")
            }
        }
    }

    override fun onFailure(
        call: Call<Profile?>, t: Throwable
    ) {
        TODO("Not yet implemented")
    }

}