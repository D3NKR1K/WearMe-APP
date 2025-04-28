package com.example.wearme.data.network.api

import android.util.Log
import androidx.core.content.edit
import com.example.wearme.data.model.LoginResponse
import com.example.wearme.data.remote.RetrofitInstance
import com.example.wearme.domain.model.TokenManager
import com.example.wearme.domain.model.api.User
import com.example.wearme.ui.auth.LoginActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginCallback(private val activity: LoginActivity, private val user: User):
    Callback<LoginResponse> {

    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
        activity.showLoading(false)

        when (response.code()) {
            200 -> {
                val loginResponse = response.body()
                val token = loginResponse?.token ?: run {
                    Log.e("[LOGIN]", "Response body is null")
                    return  // Прерываем выполнение, если тело ответа null
                }

                TokenManager(activity).saveToken(token)
                Log.i("[TOKEN SAVE]", "Token was saved")

                RetrofitInstance.initWithToken(token)

                val sharedPreferences = activity.getSharedPreferences(
                    "user_prefs", android.content.Context.MODE_PRIVATE
                )
                sharedPreferences.edit { putString("email", user.email) }
                Log.i("[EMAIL SAVE]", "Email was saved: ${user.email}")

                RetrofitInstance.bioApi.dehumanization().enqueue(CheckBioCallback(activity))
            }

            401 -> {
                Log.e("[LOGIN]", response.message())
                activity.showPasswordError()
            }

            404 -> {
                Log.i("[LOGIN]", response.message())
                activity.showEmailError()
            }

        }
    }

    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
        activity.showLoading(false)
        Log.e("LoginCallbackFailure", t.message.toString())
    }

}