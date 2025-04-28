package com.example.wearme.ui.home

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.example.wearme.databinding.ActivityClothDetailsBinding
import com.example.wearme.domain.model.api.Cloth

class ClothDetailActivity: AppCompatActivity() {
    private lateinit var binding: ActivityClothDetailsBinding

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClothDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val cloth = intent.getParcelableExtra("CLOTH_DATA", Cloth::class.java)

        cloth?.let {
            binding.clothImage.load(it.photoUrl)
            binding.clothName.text = it.name
            binding.clothRating.text = "${it.stars}"
            binding.clothMatchScore.text = "Match: ${it.matchScore}%"

            binding.measurementsChest.text = "Chest: ${it.chest ?: "N/A"} cm"
            binding.measurementsWaist.text = "Waist: ${it.waist ?: "N/A"} cm"
            binding.measurementsHips.text = "Hips: ${it.hips ?: "N/A"} cm"
            binding.measurementsFoot.text = "Foot: ${it.foot ?: "N/A"} cm"
        }
    }
}
