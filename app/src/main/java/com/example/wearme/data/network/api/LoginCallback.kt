package com.example.wearme.data.network.api

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.util.Log
import androidx.core.content.edit
import com.example.wearme.R
import com.example.wearme.data.model.LoginResponse
import com.example.wearme.data.network.retrofit.RetrofitInstance
import com.example.wearme.domain.model.TokenManager
import com.example.wearme.domain.model.api.Profile
import com.example.wearme.domain.model.api.User
import com.example.wearme.ui.auth.LoginActivity
import com.example.wearme.ui.bio.BioActivity
import com.example.wearme.ui.home.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginCallback(private val activity: LoginActivity, private val user: User):
    Callback<LoginResponse> {

    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
        activity.setLoading(false, R.id.layout_SignIn_button)

        when (response.code()) {
            200 -> {
                val loginResponse = response.body()
                val token = loginResponse?.token ?: run {
                    Log.e("[LOGIN]", "Response body is null")
                    return
                }

                TokenManager(activity).saveToken(token)
                Log.i("[TOKEN SAVE]", "Token was saved")

                RetrofitInstance.initWithToken { TokenManager(activity).getToken() }

                activity.getSharedPreferences(
                    "user_prefs", MODE_PRIVATE
                ).edit {
                    putString("email", user.email)
                    apply()
                }
                Log.i("[EMAIL SAVE]", "Email was saved: ${user.email}")

                RetrofitInstance.bioApiService.dehumanization().enqueue(object: Callback<Profile> {
                    override fun onResponse(call: Call<Profile>, response: Response<Profile>) {
                        when (response.code()) {
                            200 -> {
                                Log.i("[BIO]", "BIO WAS FOUND")

                                val name = response.body()?.name ?: "Unknown"

                                activity.getSharedPreferences(
                                    "user_prefs", MODE_PRIVATE
                                ).edit {
                                    putString("name", name)
                                    apply()
                                }

                                activity.startActivity(Intent(activity, MainActivity::class.java))
                                activity.finish()
                            }

                            404 -> {
                                Log.i("[BIO]", "BIO NOT FOUND")

                                activity.startActivity(Intent(activity, BioActivity::class.java))
                                activity.finish()
                            }

                            else -> {
                                Log.i("[BIO]", "Unexpected response: ${response.code()}")
                            }
                        }
                    }

                    override fun onFailure(call: Call<Profile>, t: Throwable) {
                        Log.e("[BIO]", "Error: ${t.message}")
                    }
                })
            }

            401, 422 -> {
                Log.e("[LOGIN]", response.message())
                activity.showError("Invalid password")
            }

            404 -> {
                Log.i("[LOGIN]", response.message())
                activity.showError("The user was not found")
            }

        }
    }

    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
        activity.setLoading(false, R.id.layout_SignIn_button)
        Log.e("LoginCallbackFailure", t.message.toString())
    }

}