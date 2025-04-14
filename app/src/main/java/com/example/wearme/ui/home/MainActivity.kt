package com.example.wearme.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.wearme.data.network.api.TokenValidationCallback
import com.example.wearme.data.remote.RetrofitInstance
import com.example.wearme.domain.model.TokenManager
import com.example.wearme.ui.auth.SignInActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity: AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    // Install splash screen to show while the app loads
    val splashScreen = installSplashScreen()
    super.onCreate(savedInstanceState)

    // Keep the splash screen visible until token validation is complete
    splashScreen.setKeepOnScreenCondition { true }

    // Launch a coroutine to handle token retrieval and validation
    lifecycleScope.launch {
      // Retrieve the token from TokenManager in the background
      val token = withContext(Dispatchers.IO) {
        TokenManager(this@MainActivity).getToken()
      }
      Log.i("[TOKEN GET]", "Token was successfully gotten")

      // Check if the token is valid by making a network call
      val isValid = withContext(Dispatchers.IO) {
        try {
          val response = RetrofitInstance.tokenApi.checkToken("Bearer $token").execute()
          response.isSuccessful
        } catch (e: Exception) {
          // Log any errors that occur during token validation
          Log.e("[TOKEN CHECK]", "Error checking token: ${e.message}")
          false
        }
      }

      // Remove the splash screen
      splashScreen.setKeepOnScreenCondition { false }

      // If the token is valid, navigate to the ProductsActivity
      if (isValid) {
        Log.i("[TOKEN CHECK]", "Token is valid")
        startActivity(Intent(this@MainActivity, ProductsActivity::class.java))
        finish()
      } else {
        // If the token is invalid, navigate to the SignInActivity
        Log.i("[TOKEN CHECK]", "Token is invalid")
        startActivity(Intent(this@MainActivity, SignInActivity::class.java))
        finish()
      }
    }
  }

}
