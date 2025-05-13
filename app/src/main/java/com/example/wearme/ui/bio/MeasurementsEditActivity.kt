package com.example.wearme.ui.bio

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.wearme.R
import com.example.wearme.data.network.api.PutMeasurementsCallback
import com.example.wearme.data.network.retrofit.RetrofitInstance
import com.example.wearme.databinding.ActivityMeasurementsEditBinding
import com.example.wearme.domain.model.FootMeasurements
import com.example.wearme.domain.model.UnderMeasurements
import com.example.wearme.domain.model.UpperMeasurements
import com.example.wearme.system.getDouble
import com.example.wearme.system.getInt
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MeasurementsEditActivity: AppCompatActivity() {

    private lateinit var binding: ActivityMeasurementsEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMeasurementsEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupValidation()
    }

    private fun setupUI() {
        binding.categoryToggle.check(R.id.btnUpper)
        updateMeasurementVisibility(R.id.btnUpper)

        binding.categoryToggle.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                updateMeasurementVisibility(checkedId)
            }
        }

        binding.layoutEnterButton.setOnClickListener {
            if (validateMeasurements()) {
                saveMeasurements()
            }
        }

        setupInstructionIcons()
    }

    private fun setupInstructionIcons() {
        binding.layoutBioChest.setEndIconOnClickListener {
            showInstructionDialog(R.string.instructionChest)
        }
        binding.layoutBioWaist.setEndIconOnClickListener {
            showInstructionDialog(R.string.instructionWaist)
        }
        binding.layoutBioHips.setEndIconOnClickListener {
            showInstructionDialog(R.string.instructionHips)
        }
        binding.layoutBioFoot.setEndIconOnClickListener {
            showInstructionDialog(R.string.instructionFoot)
        }
    }

    private fun showInstructionDialog(textResId: Int) {
        MaterialAlertDialogBuilder(this)
            .setMessage(textResId)
            .setPositiveButton(getString(R.string.measurementsOk)) { dialog, _ -> dialog.dismiss() }
            .show()
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
            binding.btnUpper.isChecked -> {
                val isChestValid = validateChest()
                val isWaistValid = validateWaist()
                val isHipsValid = validateHips()
                Log.d(
                    TAG,
                    "isValidInput: Chest valid=$isChestValid, Waist valid=isWaistValid, Hips Valid=isHipsValid"
                )
                return isChestValid && isWaistValid && isHipsValid
            }

            binding.btnLower.isChecked -> {
                val isWaistValid = validateWaist()
                val isHipsValid = validateHips()
                Log.d(TAG, "isValidInput: Waist valid=$isWaistValid, Hips Valid=isHipsValid")
                return isWaistValid && isHipsValid
            }

            binding.btnFoot.isChecked -> {
                val isFootValid = validateFoot()
                Log.d(TAG, "isValidInput: Foot valid=$isFootValid")
                return isFootValid
            }

            else -> false
        }
    }

    private fun validateFoot(): Boolean {
        return validateMeasurementFoot(binding.layoutBioFoot, "стопы", 20.0, 35.0)
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

    private fun validateMeasurementFoot(
        layout: com.google.android.material.textfield.TextInputLayout,
        fieldName: String,
        min: Double,
        max: Double
    ): Boolean {
        val value = layout.editText?.text.toString().trim()
        return when {
            value.isEmpty() -> showError(layout, "Поле $fieldName не может быть пустым")
            value.toDouble() !in min .. max -> showError(layout, "Допустимый диапазон: $min-$max")
            else -> clearError(layout)
        }
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
                    chest = binding.inputBioChest.getInt(),
                    waist = binding.inputBioWaist.getInt(),
                    hips = binding.inputBioHips.getInt()
                )
                RetrofitInstance.measurementsApiService.updateUpper(measurements)
                    .enqueue(PutMeasurementsCallback(this))
            }

            binding.btnLower.isChecked -> {
                val measurements = UnderMeasurements(
                    waist = binding.inputBioWaist.getInt(), hips = binding.inputBioHips.getInt()
                )
                RetrofitInstance.measurementsApiService.updateUnder(measurements)
                    .enqueue(PutMeasurementsCallback(this))
            }

            binding.btnFoot.isChecked -> {
                val footSize = FootMeasurements(foot = binding.inputBioFoot.getDouble())
                RetrofitInstance.measurementsApiService.updateFoot(footSize)
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

    companion object {
        private const val TAG = "MeasurementsEditActivity"
    }
}
