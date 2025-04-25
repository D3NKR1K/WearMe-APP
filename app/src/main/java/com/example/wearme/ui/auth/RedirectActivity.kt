package com.example.wearme.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.example.wearme.data.network.api.TokenValidationCallback
import com.example.wearme.data.remote.RetrofitInstance
import com.example.wearme.domain.model.TokenManager
import com.example.wearme.ui.home.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RedirectActivity: AppCompatActivity() {

    private var isLoading = true
    private val tokenValidationStatus = MutableLiveData<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition { isLoading }
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            val token = withContext(Dispatchers.IO) {
                TokenManager(this@RedirectActivity).getToken()
            }

            Log.i(TAG, "Token retrieved: ${token?.take(10)}...")

            if (token.isNullOrEmpty()) {
                Log.i(TAG, "No token found")
                isLoading = false
                navigateTo(LoginActivity::class.java)
                return@launch
            }

            withContext(Dispatchers.IO) {
                RetrofitInstance.serviceApi.checkToken("Bearer $token")
                    .enqueue(TokenValidationCallback(tokenValidationStatus))
            }

            isLoading = false

            tokenValidationStatus.observe(this@RedirectActivity) { isValid ->
                if (isValid) {
                    Log.i(TAG, "Token is valid")
                    navigateTo(MainActivity::class.java)
                } else {
                    Log.i(TAG, "Token is invalid")
                    navigateTo(LoginActivity::class.java)
                }
            }
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
