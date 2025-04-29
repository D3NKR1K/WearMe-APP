package com.example.wearme.ui.home

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
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
            binding.clothComments.text = "Reviews count: ${it.comments}"

            // Установка и скрытие мерок
            setMeasurement(binding.measurementsChest, it.chest, "Chest")
            setMeasurement(binding.measurementsWaist, it.waist, "Waist")
            setMeasurement(binding.measurementsHips, it.hips, "Hips")
            setMeasurement(binding.measurementsFoot, it.foot, "Foot")
        }

    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun setMeasurement(view: TextView, value: Number?, label: String) {
        if (value != null) {
            val formatted = when (value) {
                is Int, is Long -> value.toString()
                is Double -> {
                    if (value % 1 == 0.0) value.toInt().toString()
                    else String.format("%.1f", value)
                }

                else -> value.toString()
            }

            view.text = "$label: $formatted cm"
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }
    }


}
