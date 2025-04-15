package com.example.wearme.ui.bio

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.example.wearme.R
import com.example.wearme.data.network.api.BioCallback
import com.example.wearme.data.network.api.TokenValidationCallback
import com.example.wearme.data.remote.RetrofitInstance
import com.example.wearme.databinding.ActivityBioBinding
import com.example.wearme.domain.model.Bio
import com.example.wearme.domain.model.TokenManager
import com.google.android.material.textfield.TextInputLayout

class BioActivity: AppCompatActivity() {
  private lateinit var binding: ActivityBioBinding
  private lateinit var tokenManager: TokenManager
  private val tokenValidationStatus = MutableLiveData<Boolean>()


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityBioBinding.inflate(layoutInflater)
    setContentView(binding.root)

    tokenManager = TokenManager(this)

    val token = tokenManager.getToken() ?: run {
      Log.e("[AUTH]", "Token not found")
      return
    }

    setupValidation()

    binding.layoutBioButton.setOnClickListener {
      if (isValidInput()) {
        val (name, age, gender) = listOf(
          binding.inputBioName, binding.inputBioAge, binding.inputBioGender
        ).map { it.text.toString().trim() }

        val bio = Bio(name, age.toInt(), gender)

        showLoading(true)

        RetrofitInstance.serviceApi.checkToken("Bearer $token")
          .enqueue(TokenValidationCallback(tokenValidationStatus))

        tokenValidationStatus.observe(this) { isValid ->
          if (isValid) {
            RetrofitInstance.bioApi.humanization(bio, "Bearer $token").enqueue(BioCallback(this))
          } else {
            Log.e("[BIO]", "Invalid token")
          }
        }
      }

    }
  }

  // Настройка валидации полей ввода
  private fun setupValidation() {
    listOf(
      binding.inputBioName to ::validateName,
      binding.inputBioAge to ::validateAge,
      binding.inputBioGender to ::validateGender
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

  // Проверка валидности всех полей
  private fun isValidInput(): Boolean {
    return validateName() && validateAge() && validateGender()
  }

  private fun validateName(): Boolean {
    val name = binding.inputBioName.text.toString().trim()
    return when {
      name.isEmpty() -> showError(binding.layoutBioName, "Enter name")
      name.length > 50 -> showError(binding.layoutBioName, "Name is too long")
      else -> clearError(binding.layoutBioName)
    }
  }

  private fun validateAge(): Boolean {
    val age = binding.inputBioAge.text.toString().trim()
    return when {
      age.isEmpty() -> showError(binding.layoutBioAge, "Enter age")
      !age.matches(Regex("\\d+")) -> showError(binding.layoutBioAge, "Invalid age format")
      age.toInt() !in 1 .. 150 -> showError(binding.layoutBioAge, "Valid range: 1-150")
      else -> clearError(binding.layoutBioAge)
    }
  }

  private fun validateGender(): Boolean {
    val gender = binding.inputBioGender.text.toString().trim()
    return when {
      gender.isEmpty() -> showError(binding.layoutBioGender, "Enter gender")
      !gender.matches(Regex("[МЖмж]")) -> showError(binding.layoutBioGender, "Use M or F")
      else -> clearError(binding.layoutBioGender)
    }
  }

  // Отображение ошибки в поле
  private fun showError(layout: TextInputLayout, message: String): Boolean {
    layout.error = message
    layout.boxStrokeColor = ContextCompat.getColor(this, R.color.red)
    return false
  }


  // Сброс ошибки в поле
  private fun clearError(layout: TextInputLayout): Boolean {
    layout.error = null
    layout.boxStrokeColor = ContextCompat.getColor(this, R.color.black)
    return true
  }

  // Управление состоянием загрузки
  internal fun showLoading(isLoading: Boolean) {
    binding.layoutBioButton.isEnabled = !isLoading
  }
}
