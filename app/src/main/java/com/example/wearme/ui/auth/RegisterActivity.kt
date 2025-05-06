package com.example.wearme.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.wearme.R
import com.example.wearme.data.network.api.RegisterCallback
import com.example.wearme.data.network.retrofit.RetrofitInstance
import com.example.wearme.databinding.ActivityRegistrationBinding
import com.example.wearme.domain.model.api.User
import com.example.wearme.system.configureEmailInput
import com.example.wearme.system.configurePasswordInput
import com.example.wearme.system.getTText
import com.example.wearme.system.validateEmailField
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call

class RegisterActivity: AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding
    private var apiCall: Call<*>? = null

    // region Lifecycle Methods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: Initializing RegisterActivity")
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
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
        Log.d(TAG, "setupUI: Setting up navigation and sign-up button")

        binding.inputUserEmail.configureEmailInput()
        binding.inputUserPassword.configurePasswordInput()
        binding.inputUserPasswordRep.configurePasswordInput()

        setupNavigation()
        setupSignUpButton()
    }

    private fun setupNavigation() {
        binding.linkToSignIn.setOnClickListener {
            Log.i(TAG, "Navigating to LoginActivity")
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun setupSignUpButton() {
        binding.layoutSignUpButton.setOnClickListener {
            Log.d(TAG, "Sign-up button clicked")
            hideKeyboard()
            attemptSignUp()
        }
    }
    // endregion

    // region Validation Logic
    private fun setupValidation() {
        Log.d(TAG, "setupValidation: Adding validation listeners")
        binding.inputUserEmail.addValidationListener(::validateEmail)
        binding.inputUserPassword.addValidationListener(::validatePassword)
        binding.inputUserPasswordRep.addValidationListener(::validatePasswordRep)
    }

    private fun isValidInput(): Boolean {
        val isEmailValid = validateEmail()
        val isPasswordValid = validatePassword()
        val isPasswordRepValid = validatePasswordRep()
        Log.d(
            TAG,
            "isValidInput: Email=$isEmailValid, Password=$isPasswordValid, Repeat=$isPasswordRepValid"
        )
        return isEmailValid && isPasswordValid && isPasswordRepValid
    }

    private fun validateEmail(): Boolean {
        val email = binding.inputUserEmail.getTText()
        Log.v(TAG, "validateEmail: Validating email input")

        return validateEmailField(
            context = this, email = email, layout = binding.layoutSignUpEmail, tag = TAG
        )
    }

    private fun validatePassword(): Boolean {
        val password = binding.inputUserPassword.getTText()
        Log.v(TAG, "validatePassword: Validating password")

        return when {
            password.isEmpty() -> {
                Log.w(TAG, "validatePassword: Password is empty")
                showError(binding.layoutSignUpPassword, getString(R.string.errorEmptyPassword))
            }

            password.length < 8 -> {
                Log.w(TAG, "validatePassword: Password too short")
                showError(binding.layoutSignUpPassword, getString(R.string.errorShortPassword))
            }

            !password.contains(Regex("[A-ZА-Я]")) -> {
                Log.w(TAG, "validatePassword: No uppercase letters")
                showError(binding.layoutSignUpPassword, getString(R.string.errorNoUppercase))
            }

            else -> {
                Log.d(TAG, "validatePassword: Password is valid")
                clearError(binding.layoutSignUpPassword)
            }
        }
    }

    private fun validatePasswordRep(): Boolean {
        val password = binding.inputUserPassword.getTText()
        val passwordRep = binding.inputUserPasswordRep.getTText()
        Log.v(TAG, "validatePasswordRep: Validating password repeat")

        return when {
            passwordRep.isEmpty() -> {
                Log.w(TAG, "validatePasswordRep: Repeat password is empty")
                showError(binding.layoutSignUpPasswordRep, getString(R.string.errorEmptyPassword))
            }

            password != passwordRep -> {
                Log.w(TAG, "validatePasswordRep: Passwords don't match")
                showError(binding.layoutSignUpPasswordRep, getString(R.string.errorMatchPassword))
            }

            else -> {
                Log.d(TAG, "validatePasswordRep: Passwords match")
                clearError(binding.layoutSignUpPasswordRep)
            }
        }
    }
    // endregion

    // region Helper Methods
    private fun attemptSignUp() {
        if (isValidInput()) {
            Log.i(
                TAG, "Attempting registration with email: ${binding.inputUserEmail.getTText()}"
            )
            showLoading(true)

            val user = User(
                email = binding.inputUserEmail.getTText(),
                password = binding.inputUserPassword.getTText()
            )

            RetrofitInstance.userApiService.register(user)
                .enqueue(RegisterCallback(this@RegisterActivity))
        } else {
            Log.w(TAG, "attemptSignUp: Input validation failed")
        }
    }

    internal fun showLoading(isLoading: Boolean) {
        Log.d(TAG, "showLoading: isLoading=$isLoading")
        binding.layoutSignUpButton.isEnabled = !isLoading
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

    private fun showError(layout: TextInputLayout, message: String): Boolean {
        Log.e(TAG, "showError: ${layout.hint} - $message")
        layout.error = message
        layout.boxStrokeColor = ContextCompat.getColor(this, R.color.red)
        return false
    }

    private fun clearError(layout: TextInputLayout): Boolean {
        Log.d(TAG, "clearError: Clearing error for ${layout.hint}")
        layout.error = null
        layout.boxStrokeColor = ContextCompat.getColor(this, R.color.black)
        return true
    }
    // endregion

    companion object {
        private const val TAG = "RegisterActivity"
    }
}
