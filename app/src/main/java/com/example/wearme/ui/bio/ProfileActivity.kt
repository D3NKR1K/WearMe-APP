package com.example.wearme.ui.bio

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.example.wearme.data.network.retrofit.RetrofitInstance
import com.example.wearme.databinding.ActivityProfileBinding
import com.example.wearme.domain.model.TokenManager
import com.example.wearme.ui.auth.RedirectActivity


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
            getSharedPreferences("user_prefs", MODE_PRIVATE).edit {
                remove("name")
                remove("email")
                remove("age")
            }
            RetrofitInstance.clearAuthClient()
            TokenManager(this).clearToken() // Clear the user's token
            navigateToSignIn() // Navigate to the sign-in screen
        }

        // Edit profile button click listener
        binding.editMeasurements.setOnClickListener {
            navigateToEditMeasurements() // Navigate to the edit measurements screen
        }

        binding.editProfile.setOnClickListener {
            navigateToEditProfile() // Navigate to the edit profile screen
        }
    }

    // Navigates to the sign-in activity
    private fun navigateToSignIn() {
        startActivity(Intent(this, RedirectActivity::class.java))
        finishAffinity()
    }

    // Navigates to the edit measurements activity
    private fun navigateToEditMeasurements() {
        startActivity(Intent(this, MeasurementsEditActivity::class.java))
    }

    // Navigates to the edit profile activity
    private fun navigateToEditProfile() {
        startActivity(Intent(this, ProfileEditActivity::class.java))
    }
}
