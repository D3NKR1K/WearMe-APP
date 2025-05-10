package com.example.wearme.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.wearme.data.network.retrofit.RetrofitInstance
import com.example.wearme.domain.model.TokenManager
import com.example.wearme.domain.model.api.Profile
import com.example.wearme.ui.bio.BioActivity
import com.example.wearme.ui.home.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RedirectActivity: AppCompatActivity() {

    private var isLoading = true

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition { isLoading }
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            val tokenManager = TokenManager(this@RedirectActivity)
            val token = withContext(Dispatchers.IO) { tokenManager.getToken() }

            Log.i(TAG, "Token retrieved: ${token?.take(10)}...")

            if (token.isNullOrEmpty()) {
                Log.i(TAG, "No token found")
                isLoading = false
                navigateTo(LoginActivity::class.java)
                return@launch
            }

            RetrofitInstance.initWithToken { tokenManager.getToken() }

            RetrofitInstance.systemApiService.checkToken().enqueue(object: Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.code() == 204) {
                        Log.i(TAG, "Token is valid")

                        // Проверка наличия биометрии
                        RetrofitInstance.bioApiService.dehumanization()
                            .enqueue(object: Callback<Profile> {
                                override fun onResponse(
                                    call: Call<Profile>, response: Response<Profile>
                                ) {
                                    isLoading = false
                                    when (response.code()) {
                                        200 -> {
                                            Log.i(TAG, "BIO found")
                                            startActivity(
                                                Intent(
                                                    this@RedirectActivity, MainActivity::class.java
                                                )
                                            )
                                            finish()
                                        }

                                        401, 404 -> {
                                            Log.i(TAG, "BIO not found")
                                            startActivity(
                                                Intent(
                                                    this@RedirectActivity, BioActivity::class.java
                                                )
                                            )
                                            finish()
                                        }

                                        else -> {
                                            Log.e(TAG, "Unexpected BIO code: ${response.code()}")
                                            navigateTo(LoginActivity::class.java)
                                        }
                                    }
                                }

                                override fun onFailure(
                                    call: Call<Profile>, t: Throwable
                                ) {
                                    Log.e(TAG, "BIO request failed", t)
                                    isLoading = false
                                    navigateTo(LoginActivity::class.java)
                                }
                            })

                    } else {
                        Log.i(TAG, "Invalid token (code: ${response.code()})")
                        isLoading = false
                        navigateTo(LoginActivity::class.java)
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.e(TAG, "Token validation failed", t)
                    isLoading = false
                    navigateTo(LoginActivity::class.java)
                }
            })
        }
    }

    private fun navigateTo(destination: Class<*>) {
        startActivity(Intent(this, destination))
        finish()
    }

    companion object {
        private const val TAG = "RedirectActivity"
    }
}
