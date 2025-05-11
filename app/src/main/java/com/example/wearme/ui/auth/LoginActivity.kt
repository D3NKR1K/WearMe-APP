package com.example.wearme.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.example.wearme.R
import com.example.wearme.data.network.api.LoginCallback
import com.example.wearme.data.network.retrofit.RetrofitInstance
import com.example.wearme.databinding.ActivityLoginBinding
import com.example.wearme.domain.model.api.User
import com.example.wearme.system.*
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call

class LoginActivity: BaseActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var apiCall: Call<*>? = null

    companion object {
        private const val TAG = "LoginActivity"
        private const val NAVIGATE_REGISTER_LOG = "Navigating to RegisterActivity"
        private const val SIGN_IN_CLICK_LOG = "Sign-in button clicked"
        private const val VALIDATION_FAILED_LOG = "Input validation failed"
    }

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
        apiCall?.takeIf { !it.isCanceled }?.cancel()
    }

    private fun setupUI() {
        Log.d(TAG, "setupUI: Setting up navigation and sign-in button")
        binding.inputUserEmail.configureEmailInput()
        binding.inputUserPassword.configurePasswordInput()
        setupNavigation()
    }

    private fun setupNavigation() {
        binding.linkToSignUp.setOnClickListener {
            Log.i(TAG, NAVIGATE_REGISTER_LOG)
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.layoutSignInButton.setOnClickListener {
            Log.d(TAG, SIGN_IN_CLICK_LOG)
            attemptSignIn()
        }
    }

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
        val email = binding.inputUserEmail.getTText()
        Log.v(TAG, "validateEmail: Validating email input")
        return validateEmailField(
            context = this, email = email, layout = binding.layoutSignInEmail, tag = TAG
        )
    }

    private fun validatePassword(): Boolean {
        val password = binding.inputUserPassword.getTText()
        Log.v(TAG, "validatePassword: Validating password input")
        return if (password.isEmpty()) {
            Log.w(TAG, "validatePassword: Password is empty")
            binding.layoutSignInPassword.showError(getString(R.string.errorEmptyPassword), this)
            false
        } else {
            Log.d(TAG, "validatePassword: Password is valid")
            binding.layoutSignInPassword.clearError(this)
            true
        }
    }

    private fun attemptSignIn() {
        hideKeyboard()
        setLoading(true, R.id.layout_SignIn_button)

        binding.layoutSignInEmail.clearError(this)
        binding.layoutSignInPassword.clearError(this)

        if (isValidInput()) {
            Log.i(TAG, "Attempting sign-in with email: ${binding.inputUserEmail.getTText()}")

            val user = User(
                email = binding.inputUserEmail.getTText(),
                password = binding.inputUserPassword.getTText()
            )

            RetrofitInstance.userApiService.login(user)
                .enqueue(LoginCallback(this@LoginActivity, user))
        } else {
            Log.w(TAG, VALIDATION_FAILED_LOG)
            setLoading(false, R.id.layout_SignIn_button)
        }
    }

    private fun TextInputEditText.addValidationListener(validator: () -> Unit) {
        addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                validator()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }
}
