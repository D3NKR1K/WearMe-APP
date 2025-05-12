package com.example.wearme.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.wearme.data.network.retrofit.RetrofitInstance
import com.example.wearme.domain.model.TokenManager
import com.example.wearme.ui.bio.BioActivity
import com.example.wearme.ui.home.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call

class RedirectActivity: AppCompatActivity() {

    private var isLoading = true
    private var apiCall: Call<*>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition { isLoading }
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            handleRedirection()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: Cancelling any ongoing API call")
        apiCall?.let {
            if (!it.isCanceled) it.cancel()
        }
    }

    private suspend fun handleRedirection() {
        val tokenManager = TokenManager(this@RedirectActivity)
        val token = withContext(Dispatchers.IO) { tokenManager.getToken() }

        Log.i(TAG, "Token retrieved: ${token?.take(10)}...")

        if (token.isNullOrEmpty()) {
            Log.i(TAG, "No token found")
            isLoading = false
            navigateTo(LoginActivity::class.java)
            return
        }

        RetrofitInstance.initWithToken { tokenManager.getToken() }

        try {
            val tokenResponse = withContext(Dispatchers.IO) {
                RetrofitInstance.systemApiService.checkToken().execute()
            }

            if (tokenResponse.code() == 204) {
                Log.i(TAG, "Token is valid")
                handleBioCheck()
            } else {
                Log.i(TAG, "Invalid token (code: ${tokenResponse.code()})")
                isLoading = false
                navigateTo(LoginActivity::class.java)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Token validation failed", e)
            isLoading = false
            navigateTo(LoginActivity::class.java)
        }
    }

    private suspend fun handleBioCheck() {
        try {
            val bioResponse = withContext(Dispatchers.IO) {
                RetrofitInstance.bioApiService.dehumanization().execute()
            }

            isLoading = false
            when (bioResponse.code()) {
                200 -> {
                    Log.i(TAG, "BIO found")
                    val profile = bioResponse.body()
                    val name = profile?.name ?: "Unknown"
                    val age = profile?.age ?: 0
                    getSharedPreferences("user_prefs", MODE_PRIVATE).edit {
                        putString(
                            "name", name
                        )
                        putInt(
                            "age", age
                        )
                    }
                    navigateTo(MainActivity::class.java)
                }

                401, 404 -> {
                    Log.i(TAG, "BIO not found")
                    navigateTo(BioActivity::class.java)
                }

                else -> {
                    Log.e(TAG, "Unexpected BIO code: ${bioResponse.code()}")
                    navigateTo(LoginActivity::class.java)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "BIO request failed", e)
            isLoading = false
            navigateTo(LoginActivity::class.java)
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
