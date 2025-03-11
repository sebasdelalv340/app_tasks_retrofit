package com.example.app_api_tareas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_api_tareas.model.UsuarioRequest
import com.example.app_api_tareas.retrofit.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegistroUserViewModel : ViewModel() {

    // Estados observables para la UI
    private val _textUsername = MutableStateFlow("")
    val textUsername: StateFlow<String> = _textUsername

    private val _textPassword = MutableStateFlow("")
    val textPassword: StateFlow<String> = _textPassword

    private val _textPasswordRepeat = MutableStateFlow("")
    val textPasswordRepeat: StateFlow<String> = _textPasswordRepeat

    private val _textEmail = MutableStateFlow("")
    val textEmail: StateFlow<String> = _textEmail

    private val _textTelefono = MutableStateFlow("")
    val textTelefono: StateFlow<String> = _textTelefono

    private val _textCalle = MutableStateFlow("")
    val textCalle: StateFlow<String> = _textCalle

    private val _textNum = MutableStateFlow("")
    val textNum: StateFlow<String> = _textNum

    private val _textProvincia = MutableStateFlow("")
    val textProvincia: StateFlow<String> = _textProvincia

    private val _textMunicipio = MutableStateFlow("")
    val textMunicipio: StateFlow<String> = _textMunicipio

    private val _textCp = MutableStateFlow("")
    val textCp: StateFlow<String> = _textCp

    private val _usuarioBody = MutableStateFlow("")
    val usuarioBody: StateFlow<String> = _usuarioBody

    private val _errorBody = MutableStateFlow("")
    val errorBody: StateFlow<String> = _errorBody

    private val _errorCode = MutableStateFlow("")
    val errorCode: StateFlow<String> = _errorCode

    private val _openDialog = MutableStateFlow(false)
    val openDialog: StateFlow<Boolean> = _openDialog

    private val _resultadoRespuesta = MutableStateFlow(false)
    val resultadoRespuesta: StateFlow<Boolean> = _resultadoRespuesta

    // Actualizar estados
    fun updateUsername(username: String) {
        _textUsername.value = username
    }

    fun updatePassword(password: String) {
        _textPassword.value = password
    }

    fun updatePasswordRepeat(passwordRepeat: String) {
        _textPasswordRepeat.value = passwordRepeat
    }

    fun updateEmail(email: String) {
        _textEmail.value = email
    }

    fun updateTelefono(telefono: String) {
        _textTelefono.value = telefono
    }

    fun updateCalle(calle: String) {
        _textCalle.value = calle
    }

    fun updateNum(num: String) {
        _textNum.value = num
    }

    fun updateProvincia(provincia: String) {
        _textProvincia.value = provincia
    }

    fun updateMunicipio(municipio: String) {
        _textMunicipio.value = municipio
    }

    fun updateCp(cp: String) {
        _textCp.value = cp
    }

    // Función para realizar el registro
    fun register() {
        viewModelScope.launch(Dispatchers.IO) { // Ejecutar en un hilo de fondo
            try {
                val response = RetrofitClient.getRetrofit().register(
                    UsuarioRequest(
                        _textUsername.value,
                        _textEmail.value,
                        _textPassword.value,
                        _textPasswordRepeat.value,
                        _textTelefono.value,
                        _textCalle.value,
                        _textNum.value,
                        _textProvincia.value,
                        _textMunicipio.value,
                        _textCp.value
                    )
                )
                if (response.isSuccessful) {
                    _usuarioBody.value = response.body().toString()
                    _errorCode.value = response.code().toString()
                    _resultadoRespuesta.value = true
                } else {
                    _errorCode.value = response.code().toString()
                    _errorBody.value = response.errorBody()?.string() ?: "Error desconocido"
                    _resultadoRespuesta.value = false
                }
            } catch (e: Exception) {
                _errorBody.value = "Error en la red: ${e.localizedMessage}"
                _resultadoRespuesta.value = false
            }
            _openDialog.value = true // Mostrar el diálogo
        }
    }

    // Cerrar el diálogo de error
    fun closeDialog() {
        _openDialog.value = false
    }
}