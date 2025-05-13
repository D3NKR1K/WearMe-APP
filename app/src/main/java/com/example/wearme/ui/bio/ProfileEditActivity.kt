package com.example.wearme.ui.bio

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import com.example.wearme.R
import com.example.wearme.data.network.api.PutBioCallback
import com.example.wearme.data.network.retrofit.RetrofitInstance
import com.example.wearme.databinding.ActivityProfileEditBinding
import com.example.wearme.domain.model.api.Gender
import com.example.wearme.domain.model.api.Profile

class ProfileEditActivity: AppCompatActivity() {

    private lateinit var binding: ActivityProfileEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupValidation()
    }

    private fun setupUI() {
        binding.categoryToggle.check(R.id.btnName)
        updateDataVisibility(R.id.btnName)

        binding.categoryToggle.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                updateDataVisibility(checkedId)
            }
        }

        binding.layoutEnterButton.setOnClickListener {
            if (validateData()) {
                saveProfile()
            }
        }
    }

    private fun updateDataVisibility(checkedId: Int) {
        hideAllData()

        with(binding) {
            when (checkedId) {
                R.id.btnName -> {
                    layoutProfileName.visibility = View.VISIBLE
                }

                R.id.btnAge -> {
                    layoutProfileAge.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun hideAllData() {
        with(binding) {
            layoutProfileAge.visibility = View.GONE
            layoutProfileName.visibility = View.GONE
        }
    }

    private fun validateData(): Boolean {
        return when {
            binding.btnName.isChecked -> {
                val isNameValid = validateName()
                Log.d(
                    TAG, "isValidInput: Name valid=$isNameValid"
                )
                return isNameValid
            }

            binding.btnAge.isChecked -> {
                val isAgeValid = validateAge()
                Log.d(
                    TAG, "isValidInput: Age valid=$isAgeValid"
                )
                return isAgeValid
            }


            else -> false
        }
    }

    /*private fun validateName(): Boolean {
        val name = binding.inputProfileName.text.toString().trim()
        return when {
            name.isEmpty() -> showError(
                binding.layoutProfileName, "Enter a new name"
            )

            !name.matches(Regex("^[a-zA-Zа-яА-ЯёЁ]+$")) -> showError(
                binding.layoutProfileName, "Invalid name format"
            )

            else -> clearError(binding.layoutProfileName)
        }
    }

    private fun validateAge(): Boolean {
        val age = binding.inputProfileAge.text.toString().trim()
        return when {
            age.isEmpty() -> showError(
                binding.layoutProfileAge, "Enter an age"
            )

            !age.matches(Regex("\\d+")) -> showError(
                binding.layoutProfileAge, "Age must be a number"
            )

            age.toInt() !in 1 .. 149 -> showError(
                binding.layoutProfileAge, "Permissible age: 1-149"
            )

            else -> clearError(binding.layoutProfileAge)
        }
    }*/

    private fun validateName(): Boolean {
        val name = binding.inputProfileName.text.toString().trim()
        return when {
            name.isEmpty() -> {
                showError(binding.layoutProfileName, getString(R.string.error_enter_new_name))
            }
            !name.matches(Regex("^[a-zA-Zа-яА-ЯёЁ]+$")) -> {
                showError(binding.layoutProfileName, getString(R.string.error_invalid_name))
            }
            else -> {
                clearError(binding.layoutProfileName)
            }
        }
    }

    private fun validateAge(): Boolean {
        val age = binding.inputProfileAge.text.toString().trim()
        return when {
            age.isEmpty() -> showError(
                binding.layoutProfileAge,
                getString(R.string.error_enter_age))

            !age.matches(Regex("\\d+")) -> showError(
                binding.layoutProfileAge,
                getString(R.string.error_age_must_be_number))

            age.toInt() !in 1..149 -> showError(
                binding.layoutProfileAge,
                getString(R.string.error_permissible_age))

            else -> clearError(binding.layoutProfileAge)
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

    private fun saveProfile() {

        when {
            binding.btnName.isChecked -> {
                Log.i(TAG, "nameEdit")
                val profile = Profile(
                    name = binding.inputProfileName.text.toString().trim(),
                    age = getSharedPreferences("user_prefs", MODE_PRIVATE).getInt("age", 0).toInt(),
                    gender = Gender.M
                )

                Log.i(TAG, profile.toString())

                getSharedPreferences("user_prefs", MODE_PRIVATE).edit {
                    putString(
                        "name", profile.name
                    )
                }

                RetrofitInstance.bioApiService.rehumanization(profile).enqueue(PutBioCallback(this))
            }

            binding.btnAge.isChecked -> {
                Log.i(TAG, "ageEdit")
                val profile = Profile(
                    name = getSharedPreferences("user_prefs", MODE_PRIVATE).getString(
                        "name", "default"
                    ).toString(),
                    age = binding.inputProfileAge.text.toString().trim().toInt(),
                    gender = Gender.F
                )

                getSharedPreferences("user_prefs", MODE_PRIVATE).edit {
                    putInt(
                        "age", profile.age
                    )
                }

                RetrofitInstance.bioApiService.rehumanization(profile).enqueue(PutBioCallback(this))
            }
        }
    }

    private fun setupValidation() {
        listOf(
            binding.inputProfileName to ::validateName, binding.inputProfileAge to ::validateAge
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
        private const val TAG = "ProfileEditActivity"
    }
}