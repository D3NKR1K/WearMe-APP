package com.example.wearme.data.network.api

import android.content.Intent
import android.util.Log
import com.example.wearme.data.model.LoginResponse
import com.example.wearme.domain.model.TokenManager
import com.example.wearme.ui.auth.LoginActivity
import com.example.wearme.ui.home.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginCallback(private val activity: LoginActivity): Callback<LoginResponse> {

  override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
    activity.showLoading(false)

    val loginResponse = response.body()

    when (response.code()) {
      200 -> {
        val token = loginResponse?.token

        if (token != null) {
          TokenManager(activity).saveToken(token)
          Log.i("[TOKEN SAVE]", "Token was saved")
          activity.startActivity(Intent(activity, MainActivity::class.java))
          activity.finish()
        } else {
          Log.e("[TOKEN SAVE]", "Token saved error")
        }
      }

      401 -> {
        Log.e("[LOGIN]", response.message())
        activity.showPasswordError()
      }

      404 -> {
        Log.i("[LOGIN]", "Invalid ")
        activity.showEmailError()
      }

    }
  }

  override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
    activity.showLoading(false)
    Log.e("LoginCallbackFailure", t.message.toString())
  }

}