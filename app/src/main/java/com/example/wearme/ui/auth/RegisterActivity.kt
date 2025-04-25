package com.example.wearme.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.wearme.R
import com.example.wearme.data.network.api.RegisterCallback
import com.example.wearme.data.remote.RetrofitInstance
import com.example.wearme.databinding.ActivityRegistrationBinding
import com.example.wearme.domain.model.api.User
import com.google.android.material.textfield.TextInputEditText

class RegisterActivity: AppCompatActivity() {
    private lateinit var binding: ActivityRegistrationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            linkToSignIn.setOnClickListener {
                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                finish()
            }

            setupValidation()

            layoutSignUpButton.setOnClickListener {
                if (isValidInput()) {

                    val user = User(
                        inputUserEmail.getTrimmedText(), inputUserPassword.getTrimmedText()
                    )

                    showLoading(true)

                    RetrofitInstance.userApi.register(user)
                        .enqueue(RegisterCallback(this@RegisterActivity))

                } else {
                    Toast.makeText(
                        this@RegisterActivity, "Incorrect input data", Toast.LENGTH_SHORT
                    ).show()
                }

            }
        }
    }

    private fun isValidInput(): Boolean {
        return validateEmail() && validatePassword()
    }

    private fun setupValidation() {
        listOf(
            binding.inputUserEmail to ::validateEmail,
            binding.inputUserPassword to ::validatePassword,
            binding.inputUserPasswordRep to ::validatePassword
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

    // Валидация email пользователя
    private fun validateEmail(): Boolean {
        val email = binding.inputUserEmail.text.toString().trim()
        return when {
            email.isEmpty() -> showError(
                binding.layoutSignUpEmail, getString(R.string.error_empty_email)
            )

            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> showError(
                binding.layoutSignUpEmail, getString(R.string.error_invalid_email)
            )

            else -> clearError(binding.layoutSignUpEmail)
        }
    }

    // Валидация пароля пользователя
    private fun validatePassword(): Boolean {
        val password = binding.inputUserPassword.text.toString().trim()
        val passwordRep = binding.inputUserPasswordRep.text.toString().trim()
        return when {
            password.isEmpty() -> showError(
                binding.layoutSignUpPassword, getString(R.string.error_empty_password)
            )

            password.length < 8 -> showError(
                binding.layoutSignUpPassword, getString(R.string.error_short_password)
            )

            !password.contains(Regex("[A-Z]")) -> showError(
                binding.layoutSignUpPassword, getString(R.string.error_no_uppercase)
            )

            password != passwordRep -> showError(
                binding.layoutSignUpPassword, getString(R.string.error_match_password)
            )

            else -> clearError(binding.layoutSignUpPassword)
        }
    }

    // Показать ошибку валидации
    private fun showError(
        layout: com.google.android.material.textfield.TextInputLayout, message: String
    ): Boolean {
        layout.error = message
        layout.boxStrokeColor = ContextCompat.getColor(this, R.color.red)
        return false
    }

    // Удалить ошибку
    private fun clearError(layout: com.google.android.material.textfield.TextInputLayout): Boolean {
        layout.error = null
        layout.boxStrokeColor = ContextCompat.getColor(this, R.color.black)
        return true
    }

    // Показываем или скрываем индикатор загрузки
    internal fun showLoading(isLoading: Boolean) {
        binding.layoutSignUpButton.isEnabled = !isLoading
    }

    private fun TextInputEditText.getTrimmedText() = text.toString().trim()
}
