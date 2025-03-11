package com.example.app_api_tareas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_api_tareas.model.LoginRequest
import com.example.app_api_tareas.retrofit.RetrofitClient
import com.example.app_api_tareas.session.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel : ViewModel() {

    // Estados observables para la UI
    private val _textUser = MutableStateFlow("")
    val textUser: StateFlow<String> = _textUser

    private val _textPassword = MutableStateFlow("")
    val textPassword: StateFlow<String> = _textPassword

    private val _errorCode = MutableStateFlow("")
    val errorCode: StateFlow<String> = _errorCode

    private val _errorBody = MutableStateFlow("")
    val errorBody: StateFlow<String> = _errorBody

    private val _openDialog = MutableStateFlow(false)
    val openDialog: StateFlow<Boolean> = _openDialog

    // Actualizar el estado del usuario
    fun updateUser(user: String) {
        _textUser.value = user
    }

    // Actualizar el estado de la contraseña
    fun updatePassword(password: String) {
        _textPassword.value = password
    }

    // Función para realizar el login
    fun login(sessionManager: SessionManager, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.getRetrofit().login(LoginRequest(_textUser.value, _textPassword.value))
                if (response.isSuccessful) {
                    _errorCode.value = response.code().toString()
                    response.body()?.let { loginResponse ->
                        sessionManager.saveUserSession(_textUser.value, loginResponse.token)
                    }
                    withContext(Dispatchers.Main) { // Cambia al hilo principal para navegar
                        onSuccess()
                    }

                } else {
                    _errorCode.value = response.code().toString()
                    _errorBody.value = response.errorBody()?.string() ?: "Error desconocido"
                    _openDialog.value = true
                }
            } catch (e: Exception) {
                _errorBody.value = "Error en la red: ${e.localizedMessage}"
                _openDialog.value = true
            }
        }
    }

    // Cerrar el diálogo de error
    fun closeDialog() {
        _openDialog.value = false
    }
}