package com.example.app_api_tareas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_api_tareas.model.TareaRequest
import com.example.app_api_tareas.retrofit.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegistroTareaViewModel : ViewModel() {

    // Estados observables para la UI
    private val _textTitulo = MutableStateFlow("")
    val textTitulo: StateFlow<String> = _textTitulo

    private val _textDescripcion = MutableStateFlow("")
    val textDescripcion: StateFlow<String> = _textDescripcion

    private val _errorBody = MutableStateFlow("")
    val errorBody: StateFlow<String> = _errorBody

    private val _errorCode = MutableStateFlow("")
    val errorCode: StateFlow<String> = _errorCode

    private val _openDialog = MutableStateFlow(false)
    val openDialog: StateFlow<Boolean> = _openDialog

    private val _resultadoRespuesta = MutableStateFlow(false)
    val resultadoRespuesta: StateFlow<Boolean> = _resultadoRespuesta

    // Actualizar estados
    fun updateTitulo(titulo: String) {
        _textTitulo.value = titulo
    }

    fun updateDescripcion(descripcion: String) {
        _textDescripcion.value = descripcion
    }

    // Función para registrar una tarea
    fun registerTarea(username: String?, token: String?) {
        viewModelScope.launch(Dispatchers.IO) { // Ejecutar en un hilo de fondo
            try {
                val response = username?.let {
                    TareaRequest(
                        it,
                        _textTitulo.value,
                        _textDescripcion.value
                    )
                }?.let {
                    RetrofitClient.getRetrofit().registerTarea("Bearer $token", it)
                }
                if (response != null) {
                    if (response.isSuccessful) {
                        _errorCode.value = response.code().toString()
                        _resultadoRespuesta.value = true
                    } else {
                        _errorCode.value = response.code().toString()
                        _errorBody.value = response.errorBody()?.string() ?: "Error desconocido"
                        _resultadoRespuesta.value = false
                    }
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