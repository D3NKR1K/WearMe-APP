package com.example.wearme.ui.bio

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.example.wearme.data.network.api.GetBioCallback
import com.example.wearme.data.remote.RetrofitInstance
import com.example.wearme.databinding.ActivityProfileBinding
import com.example.wearme.domain.model.TokenManager
import com.example.wearme.domain.model.api.Profile
import com.example.wearme.ui.auth.LoginActivity

class ProfileActivity: AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var tokenManager: TokenManager
    val profileData = MutableLiveData<Profile>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tokenManager = TokenManager(this)
        val token = tokenManager.getToken() ?: run {
            Log.e("[AUTH]", "Token not found")
            finish()
            return
        }

        RetrofitInstance.bioApi.dehumanization("Bearer $token").enqueue(
            GetBioCallback(profileData)
        )

        profileData.observe(this) { profileData ->
            binding.name.text = profileData.name
        }

        setupClickListeners() // Setup button click listeners
    }

    private fun setupClickListeners() {
        // Logout button click listener
        binding.logoutButton.setOnClickListener {
            TokenManager(this).clearToken() // Clear the user's token
            navigateToSignIn() // Navigate to the sign-in screen
        }

        // Edit profile button click listener
        binding.editProfile.setOnClickListener {
            navigateToMeasurements() // Navigate to the measurements screen
        }
    }

    // Navigates to the sign-in activity
    private fun navigateToSignIn() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish() // Close the profile activity
    }

    // Navigates to the measurements activity
    private fun navigateToMeasurements() {
        startActivity(Intent(this, MeasurementsActivity::class.java))
    }
}
