package com.example.wearme.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.wearme.R
import com.example.wearme.data.network.api.BioCallbackHandler
import com.example.wearme.data.network.api.LoginCallback
import com.example.wearme.data.remote.RetrofitInstance
import com.example.wearme.databinding.ActivityLoginBinding
import com.example.wearme.domain.model.api.User
import com.example.wearme.ui.bio.BioActivity
import com.example.wearme.ui.home.MainActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call

class LoginActivity: AppCompatActivity(), BioCallbackHandler {

    private lateinit var binding: ActivityLoginBinding
    private var apiCall: Call<*>? = null

    // region Lifecycle Methods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: Initializing LoginActivity")
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUI()
        setupValidation()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: Cancelling any ongoing API call")
        apiCall?.cancel()
    }
    // endregion

    // region UI Setup
    private fun setupUI() {
        Log.d(TAG, "setupUI: Setting up navigation and sign-in button")
        setupNavigation()
        setupSignInButton()
    }

    private fun setupNavigation() {
        binding.linkToSignUp.setOnClickListener {
            Log.i(TAG, "Navigating to RegisterActivity")
            startActivity(Intent(this, RegisterActivity::class.java))
            finishAffinity()
        }
    }

    private fun setupSignInButton() {
        binding.layoutSignInButton.setOnClickListener {
            Log.d(TAG, "Sign-in button clicked")
            hideKeyboard()
            attemptSignIn()
        }
    }
    // endregion

    // region Validation Logic
    private fun setupValidation() {
        Log.d(TAG, "setupValidation: Adding validation listeners")
        binding.inputUserEmail.addValidationListener(::validateEmail)
        binding.inputUserPassword.addValidationListener(::validatePassword)
    }

    private fun isValidInput(): Boolean {
        val isEmailValid = validateEmail()
        val isPasswordValid = validatePassword()
        Log.d(TAG, "isValidInput: Email valid=$isEmailValid, Password valid=$isPasswordValid")
        return isEmailValid && isPasswordValid
    }

    private fun validateEmail(): Boolean {
        val email = binding.inputUserEmail.getTrimmedText()
        Log.v(TAG, "validateEmail: Validating email input")

        return when {
            email.isEmpty() -> {
                Log.w(TAG, "validateEmail: Email is empty")
                showError(binding.layoutSignInEmail, getString(R.string.error_empty_email))
            }

            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                Log.w(TAG, "validateEmail: Email format is invalid")
                showError(binding.layoutSignInEmail, getString(R.string.error_invalid_email))
            }

            else -> {
                Log.d(TAG, "validateEmail: Email is valid")
                clearError(binding.layoutSignInEmail)
            }
        }
    }

    private fun validatePassword(): Boolean {
        val password = binding.inputUserPassword.getTrimmedText()
        Log.v(TAG, "validatePassword: Validating password input")

        return when {
            password.isEmpty() -> {
                Log.w(TAG, "validatePassword: Password is empty")
                showError(binding.layoutSignInPassword, getString(R.string.error_empty_password))
            }

            else -> {
                Log.d(TAG, "validatePassword: Password is valid (not empty)")
                clearError(binding.layoutSignInPassword)
            }
        }
    }
    // endregion

    // region Helper Methods
    private fun attemptSignIn() {
        if (isValidInput()) {
            Log.i(TAG, "Attempting sign-in with email: ${binding.inputUserEmail.getTrimmedText()}")
            showLoading(true)

            val user = User(
                email = binding.inputUserEmail.getTrimmedText(),
                password = binding.inputUserPassword.getTrimmedText()
            )

            RetrofitInstance.userApi.login(user).enqueue(LoginCallback(this@LoginActivity))
        } else {
            Log.w(TAG, "attemptSignIn: Input validation failed")
        }
    }

    internal fun showLoading(isLoading: Boolean) {
        Log.d(TAG, "showLoading: isLoading=$isLoading")
        binding.layoutSignInButton.isEnabled = !isLoading
    }

    private fun hideKeyboard() {
        Log.d(TAG, "hideKeyboard: Hiding keyboard")
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }
    // endregion

    // region View Extensions
    private fun TextInputEditText.addValidationListener(validator: () -> Unit) {
        addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                Log.v(TAG, "Text changed in ${this@addValidationListener.id}")
                validator()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun TextInputEditText.getTrimmedText() = text.toString().trim()

    private fun showError(layout: TextInputLayout, message: String): Boolean {
        Log.e(TAG, "showError: ${layout.hint} - $message")
        layout.error = message
        layout.boxStrokeColor = ContextCompat.getColor(this, R.color.red)
        return false
    }

    internal fun showEmailError() {
        Log.e(TAG, "showEmailError: The user was not found")
        binding.layoutSignInEmail.error = "The user was not found"
        binding.layoutSignInEmail.boxStrokeColor = ContextCompat.getColor(this, R.color.red)
    }

    internal fun showPasswordError() {
        Log.e(TAG, "showPasswordError: Invalid password")
        binding.layoutSignInPassword.error = "Invalid password"
        binding.layoutSignInPassword.boxStrokeColor = ContextCompat.getColor(this, R.color.red)
    }

    private fun clearError(layout: TextInputLayout): Boolean {
        Log.d(TAG, "clearError: Clearing error for ${layout.hint}")
        layout.error = null
        layout.boxStrokeColor = ContextCompat.getColor(this, R.color.black)
        return true
    }

    override fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun navigateToBio() {
        startActivity(Intent(this, BioActivity::class.java))
        finish()
    }
    // endregion

    companion object {
        private const val TAG = "LoginActivity"
    }

}
