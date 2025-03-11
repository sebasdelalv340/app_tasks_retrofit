package com.example.app_api_tareas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_api_tareas.model.Estado
import com.example.app_api_tareas.model.TareaResponseDTO
import com.example.app_api_tareas.retrofit.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MisTareasViewModel : ViewModel() {

    // Estados observables para la UI
    private val _tareas = MutableStateFlow<List<TareaResponseDTO>>(emptyList())
    val tareas: StateFlow<List<TareaResponseDTO>> = _tareas

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // Función para cargar las tareas
    fun cargarTareas(username: String?, token: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            if (username != null && token != null) {
                _isLoading.value = true
                _errorMessage.value = null

                try {
                    val response = RetrofitClient.getRetrofit().obtenerTareas("Bearer $token", username)
                    if (response.isSuccessful) {
                        _tareas.value = response.body() ?: emptyList()
                    } else {
                        _errorMessage.value = "Error al obtener las tareas: ${response.errorBody()?.string()}"
                    }
                } catch (e: Exception) {
                    _errorMessage.value = "Error en la red: ${e.localizedMessage}"
                } finally {
                    _isLoading.value = false
                }
            } else {
                _errorMessage.value = "No se encontró el nombre de usuario"
            }
        }
    }

    // Función para borrar una tarea
    fun borrarTarea(username: String?, token: String?, titulo: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.getRetrofit().borrarTarea("Bearer $token", titulo)
                if (response.isSuccessful) {
                    // Recargar la lista de tareas después de borrar
                    cargarTareas(username, token)
                } else {
                    _errorMessage.value = "Error al borrar la tarea: ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error en la red: ${e.localizedMessage}"
            }
        }
    }

    // Función para cambiar el estado de una tarea
    fun cambiarEstadoTarea(username: String?, token: String?, titulo: String, nuevoEstado: Estado) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.getRetrofit().cambiarEstado("Bearer $token", titulo, nuevoEstado)
                if (response.isSuccessful) {
                    // Recargar la lista de tareas después de cambiar el estado
                    cargarTareas(username, token)
                } else {
                    _errorMessage.value = "Error al cambiar el estado de la tarea: ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error en la red: ${e.localizedMessage}"
            }
        }
    }
}