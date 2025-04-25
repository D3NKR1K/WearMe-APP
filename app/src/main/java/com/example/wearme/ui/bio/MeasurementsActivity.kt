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
import com.example.wearme.domain.model.TokenManager
import com.example.wearme.domain.model.UnderMeasurements
import com.example.wearme.domain.model.UpperMeasurements

class MeasurementsActivity: AppCompatActivity() {

    private lateinit var binding: ActivityMeasurementsBinding
    private lateinit var tokenManager: TokenManager
    private val tokenValidationStatus = MutableLiveData<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMeasurementsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tokenManager = TokenManager(this)

        val token = tokenManager.getToken() ?: run {
            Log.e("[AUTH]", "Token not found")
            return
        }

        RetrofitInstance.serviceApi.checkToken("Bearer $token")
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
        updateMeasurementVisibility(true)

        // Настройка переключателя категорий
        binding.categoryToggle.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                val isUpper = checkedId == R.id.btnUpper
                updateMeasurementVisibility(isUpper)
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
        }
    }

    private fun updateMeasurementVisibility(isUpper: Boolean) {
        hideAllMeasurements()

        with(binding) {
            if (isUpper) {
                layoutBioHips.visibility = View.VISIBLE
                layoutBioChest.visibility = View.VISIBLE
                layoutBioWaist.visibility = View.VISIBLE
                layoutBioHipsl.visibility = View.GONE
                layoutBioWaistl.visibility = View.GONE
            } else {
                layoutBioHips.visibility = View.GONE
                layoutBioChest.visibility = View.GONE
                layoutBioWaist.visibility = View.GONE
                layoutBioHipsl.visibility = View.VISIBLE
                layoutBioWaistl.visibility = View.VISIBLE
            }
        }
    }

    private fun validateMeasurements(): Boolean {
        return if (binding.btnUpper.isChecked) {
            validateChest() && validateWaist() && validateHips()
        } else {
            validateWaistl() && validateHipsl()
        }
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

    private fun validateWaistl(): Boolean {
        return validateMeasurement(binding.layoutBioWaistl, "талии", 50, 150)
    }

    private fun validateHipsl(): Boolean {
        return validateMeasurement(binding.layoutBioHipsl, "бедер", 70, 200)
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
        val token = tokenManager.getToken()

        if (binding.btnUpper.isChecked) {
            val measurements = UpperMeasurements(
                chest = binding.inputBioChest.text.toString().toInt(),
                waist = binding.inputBioWaist.text.toString().toInt(),
                hips = binding.inputBioHips.text.toString().toInt()
            )

            RetrofitInstance.measurementsApi.updateUpper(measurements, "Bearer $token").enqueue(
                PutMeasurementsCallback(this)
            )
        } else {
            val measurements = UnderMeasurements(
                waist = binding.inputBioWaistl.text.toString().toInt(),
                hips = binding.inputBioHipsl.text.toString().toInt()
            )

            RetrofitInstance.measurementsApi.updateUnder(measurements, "Bearer $token").enqueue(
                PutMeasurementsCallback(this)
            )
        }
    }

    private fun setupValidation() {
        listOf(
            binding.inputBioChest to ::validateChest,
            binding.inputBioWaist to ::validateWaist,
            binding.inputBioHips to ::validateHips,
            binding.inputBioWaistl to ::validateWaistl,
            binding.inputBioHipsl to ::validateHipsl
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