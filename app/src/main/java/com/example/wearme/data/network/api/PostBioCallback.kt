package com.example.wearme.data.network.api

import android.content.Intent
import android.util.Log
import com.example.wearme.ui.auth.LoginActivity
import com.example.wearme.ui.bio.BioActivity
import com.example.wearme.ui.home.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostBioCallback(private val activity: BioActivity): Callback<Void> {

    override fun onResponse(
        call: Call<Void?>, response: Response<Void?>
    ) {
        activity.showLoading(false)

        when (response.code()) {
            // Успешное обновление данных
            201 -> {
                Log.i("[BIO]", "Данные успешно сохранены")
                activity.startActivity(Intent(activity, MainActivity::class.java))
                activity.finish()
            }

            // Ошибка авторизации
            401 -> {
                Log.e("[BIO]", "Токен недействителен")
            }

            // Данные не найдены
            404 -> {
                Log.w("[BIO]", "Данные не найдены")
            }

            // Остальные ошибки
            else -> {
                val errorMessage = response.errorBody()?.string() ?: "Неизвестная ошибка"
                Log.e("[BIO]", "Код ${response.code()}: $errorMessage")
            }
        }
    }

    override fun onFailure(call: Call<Void?>, t: Throwable) {
        activity.showLoading(false)
        Log.e("[BIO]", "Сетевая ошибка: ${t.message}")
    }

    private fun navigateToLogin() {
        val intent = Intent(activity, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        activity.startActivity(intent)
        activity.finish()
    }
}