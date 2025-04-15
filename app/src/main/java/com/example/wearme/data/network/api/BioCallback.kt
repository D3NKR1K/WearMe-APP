package com.example.wearme.data.network.api

import com.example.wearme.data.model.HumanizationResponse
import com.example.wearme.ui.bio.BioActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BioCallback(private val activity: BioActivity): Callback<HumanizationResponse> {
  override fun onResponse(
    call: Call<HumanizationResponse?>, response: Response<HumanizationResponse?>
  ) {
    activity.showLoading(false)

    when (response.code()) {
      204 -> {
        activity
      }
    }
  }

  override fun onFailure(
    call: Call<HumanizationResponse?>, t: Throwable
  ) {
    TODO("Not yet implemented")
  }

}