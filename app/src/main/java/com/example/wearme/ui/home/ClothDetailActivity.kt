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
            binding.apply {
                val photoUrl = it.photoUrl.replace(Regex("\\d+\\.webp$"), "1.webp")
                clothImage.load(photoUrl)
                clothName.text = it.name
                clothRating.text = "${it.stars}"
                clothMatchScore.text = "Match: ${it.variation.matchScore}%"
                clothComments.text = "Reviews count: ${it.comments}"

                it.variation.let { variation ->
                    setMeasurement(measurementsChest, variation.chest, "Chest")
                    setMeasurement(measurementsWaist, variation.waist, "Waist")
                    setMeasurement(measurementsHips, variation.hips, "Hips")
                    setMeasurement(measurementsFoot, variation.foot, "Foot")
                }
            }
        }
    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun setMeasurement(view: TextView, value: Number?, label: String) {
        value?.let {
            view.text = "$label: ${formatValue(it)} cm"
            view.visibility = View.VISIBLE
        } ?: run {
            view.visibility = View.GONE
        }
    }

    @SuppressLint("DefaultLocale")
    private fun formatValue(value: Number): String {
        return if (value is Float) String.format("%.1f", value) else value.toString()
    }
}
