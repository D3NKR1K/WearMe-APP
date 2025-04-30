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
            // Загрузка основной информации
            binding.clothImage.load(it.photoUrl)
            binding.clothName.text = it.name
            binding.clothRating.text = "${it.stars}"
            binding.clothMatchScore.text = "Match: ${it.matchScore}%"
            binding.clothComments.text = "Reviews count: ${it.comments}"

            // Получаем первую вариацию (или null)
            val bestVariation = it.variations.maxByOrNull { variation -> variation.matchScore }

            // Установка мерок из вариации
            bestVariation?.let { variation ->
                setMeasurement(binding.measurementsChest, variation.chest, "Chest")
                setMeasurement(binding.measurementsWaist, variation.waist, "Waist")
                setMeasurement(binding.measurementsHips, variation.hips, "Hips")
                setMeasurement(binding.measurementsFoot, variation.foot, "Foot")
            }
        }
    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun setMeasurement(view: TextView, value: Number?, label: String) {
        if (value != null) {
            val formatted = when (value) {
                is Float -> String.format("%.1f", value)
                else -> value.toString()
            }
            view.text = "$label: $formatted cm"
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }
    }
}