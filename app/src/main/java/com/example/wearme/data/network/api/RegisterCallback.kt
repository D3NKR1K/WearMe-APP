package com.example.wearme.data.network.api

import android.content.Intent
import android.util.Log
import com.example.wearme.data.model.MessageResponse
import com.example.wearme.ui.auth.LoginActivity
import com.example.wearme.ui.auth.RegisterActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterCallback(private val activity: RegisterActivity): Callback<MessageResponse> {
    override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
        activity.showLoading(false)

        when (response.code()) {
            201 -> {
                Log.i("[REGISTER]", "Register success")
                activity.startActivity(Intent(activity, LoginActivity::class.java))
                activity.finish()
            }

            409 -> {
                Log.e("[REGISTER]", response.message())
            }

            500 -> {
                Log.e("[REGISTER]", response.message())
            }
        }
    }

    override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
        activity.showLoading(false)
        Log.e("[REGISTER]", t.message.toString())
    }

}