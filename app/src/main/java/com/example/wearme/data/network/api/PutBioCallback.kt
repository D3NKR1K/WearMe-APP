package com.example.wearme.data.network.api

import android.content.Intent
import android.util.Log
import com.example.wearme.ui.bio.ProfileEditActivity
import com.example.wearme.ui.home.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PutBioCallback(private val activity: ProfileEditActivity): Callback<Void> {

    override fun onResponse(
        call: Call<Void>, response: Response<Void>
    ) {
        when (response.code()) {
            // Успешное обновление данных
            204 -> {
                Log.i("[PROFILE UPDATE]", "Данные успешно сохранены")
                activity.startActivity(Intent(activity, MainActivity::class.java))
                activity.finish()
            }

            422 -> {
                Log.e("[PROFILE UPDATE]", "Profile was not updated")
            }

            // Остальные ошибки
            else -> {
                val errorMessage = response.errorBody()?.string() ?: "Неизвестная ошибка"
                Log.e("[PROFILE UPDATE]", "Код ${response.code()}: $errorMessage")
            }
        }
    }

    override fun onFailure(call: Call<Void>, t: Throwable) {
        Log.e("[BIO]", "Сетевая ошибка: ${t.message}")
    }
}