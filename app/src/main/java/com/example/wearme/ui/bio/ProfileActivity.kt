package com.example.wearme.ui.bio

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.wearme.databinding.ActivityProfileBinding
import com.example.wearme.domain.model.TokenManager
import com.example.wearme.ui.auth.SignInActivity

class ProfileActivity: AppCompatActivity() {
  private lateinit var binding: ActivityProfileBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = ActivityProfileBinding.inflate(layoutInflater)
    setContentView(binding.root)

    binding.logoutButton.setOnClickListener {
      TokenManager(this).clearToken()
      startActivity(Intent(this, SignInActivity::class.java))
      finish()
    }

    binding.editProfile.setOnClickListener {
      startActivity(Intent(this, MeasurementsActivity::class.java))
      finish()
    }
  }
}
