package com.example.wearme.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.wearme.R
import com.example.wearme.data.network.api.LoginCallback
import com.example.wearme.data.remote.RetrofitInstance
import com.example.wearme.databinding.ActivityLoginBinding
import com.example.wearme.domain.model.User
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call

class SignInActivity: AppCompatActivity() {

  private lateinit var binding: ActivityLoginBinding
  private var apiCall: Call<*>? = null

  // region Lifecycle Methods
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityLoginBinding.inflate(layoutInflater)
    setContentView(binding.root)
    setupUI()
    setupValidation()
  }

  override fun onDestroy() {
    super.onDestroy()
    apiCall?.cancel()
  }
  // endregion

  // region UI Setup
  private fun setupUI() {
    setupNavigation()
    setupSignInButton()
  }

  private fun setupNavigation() {
    binding.linkToSignUp.setOnClickListener {
      startActivity(Intent(this, SignUpActivity::class.java))
      finish()
    }
  }

  private fun setupSignInButton() {
    binding.layoutSignInButton.setOnClickListener {
      hideKeyboard()
      attemptSignIn()
    }
  }
  // endregion

  // region Validation Logic
  private fun setupValidation() {
    binding.inputUserEmail.addValidationListener(::validateEmail)
    binding.inputUserPassword.addValidationListener(::validatePassword)
  }

  private fun isValidInput(): Boolean {
    val isEmailValid = validateEmail()
    val isPasswordValid = validatePassword()
    return isEmailValid && isPasswordValid
  }

  private fun validateEmail(): Boolean {
    val email = binding.inputUserEmail.getTrimmedText()

    return when {
      email.isEmpty() -> {
        showError(binding.layoutSignInEmail, getString(R.string.error_empty_email))
      }

      !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> showError(
        binding.layoutSignInEmail, getString(R.string.error_invalid_email)
      )

      else -> clearError(binding.layoutSignInEmail)
    }
  }

  private fun validatePassword(): Boolean {
    val password = binding.inputUserPassword.getTrimmedText()

    return when {
      password.isEmpty() -> {
        showError(binding.layoutSignInPassword, getString(R.string.error_empty_password))
      }

      else -> clearError(binding.layoutSignInPassword)
    }
  }
  // endregion

  // region Helper Methods
  private fun attemptSignIn() {
    if (isValidInput()) {
      showLoading(true)
      val user = User(
        email = binding.inputUserEmail.getTrimmedText(),
        password = binding.inputUserPassword.getTrimmedText()
      )
      apiCall = RetrofitInstance.authorizationAPI.login(user).apply {
        enqueue(LoginCallback(this@SignInActivity))
      }
    }
  }

  internal fun showLoading(isLoading: Boolean) {
    binding.layoutSignInButton.isEnabled = !isLoading
  }

  private fun hideKeyboard() {
    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
  }
  // endregion

  // region View Extensions
  private fun TextInputEditText.addValidationListener(validator: () -> Unit) {
    addTextChangedListener(object: TextWatcher {
      override fun afterTextChanged(s: Editable?) = validator()
      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })
  }

  private fun TextInputEditText.getTrimmedText() = text.toString().trim()

  fun showError(layout: TextInputLayout, message: String): Boolean {
    layout.error = message
    layout.boxStrokeColor = ContextCompat.getColor(this, R.color.red)
    return false
  }

  fun showEmailError() {
    binding.layoutSignInEmail.error = "The user was not found"
    binding.layoutSignInEmail.boxStrokeColor = ContextCompat.getColor(this, R.color.red)
  }

  fun showPasswordError() {
    binding.layoutSignInPassword.error = "Invalid password"
    binding.layoutSignInPassword.boxStrokeColor = ContextCompat.getColor(this, R.color.red)
  }

  private fun clearError(layout: TextInputLayout): Boolean {
    layout.error = null
    layout.boxStrokeColor = ContextCompat.getColor(this, R.color.black)
    return true
  }
  // endregion

}
