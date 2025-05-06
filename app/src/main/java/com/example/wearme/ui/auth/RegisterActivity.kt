package com.example.wearme.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.example.wearme.R
import com.example.wearme.data.network.api.RegisterCallback
import com.example.wearme.data.network.retrofit.RetrofitInstance
import com.example.wearme.databinding.ActivityRegistrationBinding
import com.example.wearme.domain.model.api.User
import com.example.wearme.system.*
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call

class RegisterActivity: BaseActivity() {

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
                binding.layoutSignUpPassword.showError(getString(R.string.errorEmptyPassword), this)
            }

            password.length < 8 -> {
                Log.w(TAG, "validatePassword: Password too short")
                binding.layoutSignUpPassword.showError(getString(R.string.errorShortPassword), this)
            }

            !password.contains(Regex("[A-ZА-Я]")) -> {
                Log.w(TAG, "validatePassword: No uppercase letters")
                binding.layoutSignUpPassword.showError(getString(R.string.errorNoUppercase), this)
            }

            else -> {
                Log.d(TAG, "validatePassword: Password is valid")
                binding.layoutSignUpPassword.clearError(this)
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
                binding.layoutSignUpPasswordRep.showError(
                    getString(R.string.errorEmptyPassword), this
                )
            }

            password != passwordRep -> {
                Log.w(TAG, "validatePasswordRep: Passwords don't match")
                binding.layoutSignUpPasswordRep.showError(
                    getString(R.string.errorMatchPassword), this
                )
            }

            else -> {
                Log.d(TAG, "validatePasswordRep: Passwords match")
                binding.layoutSignUpPasswordRep.clearError(this)
            }
        }
    }
    // endregion

    // region Helper Methods
    private fun attemptSignUp() {
        hideKeyboard()
        setLoading(true, R.id.layout_SignUp_button)

        binding.layoutSignUpEmail.clearError(this)
        binding.layoutSignUpPassword.clearError(this)
        binding.layoutSignUpPasswordRep.clearError(this)
        if (isValidInput()) {
            Log.i(
                TAG, "Attempting registration with email: ${binding.inputUserEmail.getTText()}"
            )

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
    // endregion

    companion object {
        private const val TAG = "RegisterActivity"
    }
}
