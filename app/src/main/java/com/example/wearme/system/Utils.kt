package com.example.wearme.system


import android.content.Context
import android.text.InputFilter
import android.text.InputType
import android.util.Log
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import com.example.wearme.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.apache.commons.validator.routines.EmailValidator


fun validateEmailField(
    context: Context, email: String, layout: TextInputLayout, tag: String
): Boolean {
    val validator = EmailValidator.getInstance(false, false)

    return when {
        email.isEmpty() -> {
            Log.w(tag, "validateEmail: Email is empty")
            layout.error = context.getString(R.string.errorEmptyEmail)
            false
        }

        !validator.isValid(email) -> {
            Log.w(tag, "validateEmail: Email format is invalid")
            layout.error = context.getString(R.string.errorInvalidEmail)
            false
        }

        else -> {
            Log.d(tag, "validateEmail: Email is valid")
            layout.error = null
            true
        }
    }
}

fun TextInputEditText.configureEmailInput() {
    inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
    imeOptions = EditorInfo.IME_FLAG_FORCE_ASCII
    filters = arrayOf(InputFilter { source, _, _, _, _, _ ->
        val allowedRegex = Regex("[a-zA-Z0-9@._\\-+]")
        val filtered = source.filter { it.toString().matches(allowedRegex) }
        if (filtered.length == source.length) null else filtered
    })
}

fun TextInputEditText.configurePasswordInput() {
    filters = arrayOf(InputFilter { source, _, _, _, _, _ ->
        source.toString().replace(" ", "")
    })
}

fun TextInputLayout.showError(message: String, context: Context): Boolean {
    error = message
    boxStrokeColor = ContextCompat.getColor(context, R.color.red)
    return false
}

fun TextInputLayout.clearError(context: Context): Boolean {
    error = null
    boxStrokeColor = ContextCompat.getColor(context, R.color.black)
    return true
}