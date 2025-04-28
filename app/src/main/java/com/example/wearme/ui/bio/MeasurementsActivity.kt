package com.example.wearme.ui.bio

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.example.wearme.R
import com.example.wearme.data.network.api.PutMeasurementsCallback
import com.example.wearme.data.network.api.TokenValidationCallback
import com.example.wearme.data.remote.RetrofitInstance
import com.example.wearme.databinding.ActivityMeasurementsBinding
import com.example.wearme.domain.model.FootMeasurements
import com.example.wearme.domain.model.UnderMeasurements
import com.example.wearme.domain.model.UpperMeasurements

class MeasurementsActivity: AppCompatActivity() {

    private lateinit var binding: ActivityMeasurementsBinding
    private val tokenValidationStatus = MutableLiveData<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMeasurementsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        RetrofitInstance.serviceApi.checkToken()
            .enqueue(TokenValidationCallback(tokenValidationStatus))

        tokenValidationStatus.observe(this) { isValid ->
            if (isValid) {
                setupUI()
                setupValidation()
            } else {
                Log.e("[AUTH]", "Invalid token")
            }
        }
    }

    private fun setupUI() {
        binding.categoryToggle.check(R.id.btnUpper)
        updateMeasurementVisibility(R.id.btnUpper)

        // Настройка переключателя категорий
        binding.categoryToggle.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                updateMeasurementVisibility(checkedId)
            }
        }

        // Обработчик кнопки сохранения
        binding.layoutEnterButton.setOnClickListener {
            if (validateMeasurements()) {
                saveMeasurements()
            }
        }
    }

    private fun hideAllMeasurements() {
        with(binding) {
            layoutBioChest.visibility = View.GONE
            layoutBioWaist.visibility = View.GONE
            layoutBioHips.visibility = View.GONE
            layoutBioFoot.visibility = View.GONE
        }
    }

    private fun updateMeasurementVisibility(checkedId: Int) {
        hideAllMeasurements()

        with(binding) {
            when (checkedId) {
                R.id.btnUpper -> {
                    layoutBioHips.visibility = View.VISIBLE
                    layoutBioChest.visibility = View.VISIBLE
                    layoutBioWaist.visibility = View.VISIBLE
                }

                R.id.btnLower -> {
                    layoutBioHips.visibility = View.VISIBLE
                    layoutBioWaist.visibility = View.VISIBLE
                }

                R.id.btnFoot -> {
                    layoutBioFoot.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun validateMeasurements(): Boolean {
        return when {
            binding.btnUpper.isChecked -> validateChest() && validateWaist() && validateHips()
            binding.btnLower.isChecked -> validateWaist() && validateHips()
            binding.btnFoot.isChecked -> validateFoot()
            else -> false
        }
    }

    private fun validateFoot(): Boolean {
        return validateMeasurement(binding.layoutBioFoot, "стопы", 20, 35)
    }

    private fun validateChest(): Boolean {
        return validateMeasurement(binding.layoutBioChest, "груди", 50, 150)
    }

    private fun validateWaist(): Boolean {
        return validateMeasurement(binding.layoutBioWaist, "талии", 50, 150)
    }

    private fun validateHips(): Boolean {
        return validateMeasurement(binding.layoutBioHips, "бедер", 70, 200)
    }

    private fun validateMeasurement(
        layout: com.google.android.material.textfield.TextInputLayout,
        fieldName: String,
        min: Int,
        max: Int
    ): Boolean {
        val value = layout.editText?.text.toString().trim()
        return when {
            value.isEmpty() -> showError(layout, "Поле $fieldName не может быть пустым")
            !value.matches(Regex("\\d+")) -> showError(layout, "Значение должно быть целым числом")
            value.toInt() !in min .. max -> showError(layout, "Допустимый диапазон: $min-$max")
            else -> clearError(layout)
        }
    }

    private fun showError(
        layout: com.google.android.material.textfield.TextInputLayout, message: String
    ): Boolean {
        layout.error = message
        layout.boxStrokeColor = ContextCompat.getColor(this, R.color.red)
        return false
    }

    private fun clearError(layout: com.google.android.material.textfield.TextInputLayout): Boolean {
        layout.error = null
        layout.boxStrokeColor = ContextCompat.getColor(this, R.color.black)
        return true
    }

    private fun saveMeasurements() {
        when {
            binding.btnUpper.isChecked -> {
                val measurements = UpperMeasurements(
                    chest = binding.inputBioChest.text.toString().toInt(),
                    waist = binding.inputBioWaist.text.toString().toInt(),
                    hips = binding.inputBioHips.text.toString().toInt()
                )
                RetrofitInstance.measurementsApi.updateUpper(measurements)
                    .enqueue(PutMeasurementsCallback(this))
            }

            binding.btnLower.isChecked -> {
                val measurements = UnderMeasurements(
                    waist = binding.inputBioWaist.text.toString().toInt(),
                    hips = binding.inputBioHips.text.toString().toInt()
                )
                RetrofitInstance.measurementsApi.updateUnder(measurements)
                    .enqueue(PutMeasurementsCallback(this))
            }

            binding.btnFoot.isChecked -> {
                val footSize =
                    FootMeasurements(foot = binding.inputBioFoot.text.toString().toDouble())
                RetrofitInstance.measurementsApi.updateFoot(footSize)
                    .enqueue(PutMeasurementsCallback(this))
            }
        }
    }

    private fun setupValidation() {
        listOf(
            binding.inputBioChest to ::validateChest,
            binding.inputBioWaist to ::validateWaist,
            binding.inputBioHips to ::validateHips,
            binding.inputBioFoot to ::validateFoot
        ).forEach { (field, validator) ->
            field.addTextChangedListener(object: TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    validator()
                }

                override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            })
        }
    }
}