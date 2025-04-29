package com.example.wearme.ui.bio

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.wearme.databinding.ActivityProfileBinding
import com.example.wearme.domain.model.TokenManager
import com.example.wearme.ui.auth.LoginActivity

class ProfileActivity: AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)

        binding.name.text = sharedPreferences.getString("name", "No name found")
        binding.email.text = sharedPreferences.getString("email", "No email found")

        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Logout button click listener
        binding.logoutButton.setOnClickListener {
            TokenManager(this).clearToken() // Clear the user's token
            navigateToSignIn() // Navigate to the sign-in screen
        }

        // Edit profile button click listener
        binding.editMeasurements.setOnClickListener {
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
