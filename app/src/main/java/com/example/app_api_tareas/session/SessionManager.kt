package com.example.app_api_tareas.session

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SessionManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USERNAME = "username"
        private const val KEY_TOKEN = "token"
    }

    // Guardar el username y el token
    fun saveUserSession(username: String, token: String) {
        sharedPreferences.edit() {
            putString(KEY_USERNAME, username)
            putString(KEY_TOKEN, token)
        }
    }

    // Obtener el username
    fun getUsername(): String? {
        return sharedPreferences.getString(KEY_USERNAME, null)
    }

    // Obtener el token
    fun getToken(): String? {
        return sharedPreferences.getString(KEY_TOKEN, null)
    }

    // Limpiar la sesión (para logout)
    fun clearSession() {
        sharedPreferences.edit() {
            clear()
        }
    }
}