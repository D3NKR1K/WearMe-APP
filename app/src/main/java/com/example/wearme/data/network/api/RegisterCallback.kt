package com.example.wearme.data.network.api

import android.content.Intent
import android.util.Log
import com.example.wearme.data.model.RegisterResponse
import com.example.wearme.ui.auth.LoginActivity
import com.example.wearme.ui.auth.RegisterActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterCallback(private val activity: RegisterActivity): Callback<RegisterResponse> {
  override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
    activity.showLoading(false)

    when (response.code()) {
      201 -> {
        activity.startActivity(Intent(activity, LoginActivity::class.java))
        activity.finish()
      }

      409 -> {
        Log.e("[REGISTER RESPONSE]", response.message())
      }
    }
  }

  override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
    activity.showLoading(false)
    Log.e("[REGISTER CALLBACK]", t.message.toString())
  }

}