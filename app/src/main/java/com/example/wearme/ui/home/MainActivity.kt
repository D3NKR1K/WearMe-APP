package com.example.wearme.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.wearme.data.network.api.TokenValidationCallback
import com.example.wearme.data.remote.RetrofitInstance
import com.example.wearme.domain.model.TokenManager
import com.example.wearme.ui.auth.SignInActivity

class MainActivity: AppCompatActivity() {

  private val tokenValidationStatus = MutableLiveData<Boolean>()

  override fun onCreate(savedInstanceState: Bundle?) {
    val splashScreen = installSplashScreen()

    super.onCreate(savedInstanceState)

    splashScreen.setKeepOnScreenCondition { true }

    val token = TokenManager(this).getToken()
    Log.i("[TOKEN GET]", "Token was successfully gotten")

    RetrofitInstance.tokenApi.checkToken("Bearer $token")
      .enqueue(TokenValidationCallback(tokenValidationStatus))

    tokenValidationStatus.observe(this, Observer { isValid ->
      if (isValid) {
        Log.i("[MAIN]", "Redirecting to ProductsActivity")
        startActivity(Intent(this, ProductsActivity::class.java))
        finish()
      } else {
        Log.i("[MAIN]", "Redirecting to SignInActivity")
        startActivity(Intent(this, SignInActivity::class.java))
        finish()
      }
    })
  }

}