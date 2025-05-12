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
    private val sharedPreferences by lazy { getSharedPreferences("user_prefs", MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadUserData()
        setupClickListeners()
    }

    private fun loadUserData() {
        binding.name.text = sharedPreferences.getString("name", "No name found")
        binding.email.text = sharedPreferences.getString("email", "No email found")
    }

    private fun handleLogout() {
        sharedPreferences.edit {
            remove("name")
            remove("email")
            remove("age")
        }
        RetrofitInstance.clearAuthClient()
        TokenManager(this).clearToken()
        navigateToRedirect()
    }

    private fun setupClickListeners() {
        binding.logoutButton.setOnClickListener {
            handleLogout()
        }

        binding.editMeasurements.setOnClickListener {
            navigateToEditMeasurements()
        }

        binding.editProfile.setOnClickListener {
            navigateToEditProfile()
        }
    }

    private fun navigateToRedirect() {
        startActivity(Intent(this, RedirectActivity::class.java))
        finishAffinity()
    }

    private fun navigateToEditMeasurements() {
        startActivity(Intent(this, MeasurementsEditActivity::class.java))
    }

    private fun navigateToEditProfile() {
        startActivity(Intent(this, ProfileEditActivity::class.java))
    }
}
