package com.example.app_api_tareas.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.app_api_tareas.model.EstadoTarea
import com.example.app_api_tareas.model.TareaResponseDTO
import com.example.app_api_tareas.retrofit.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun MisTareas(modifier: Modifier, navController: NavController) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val username = sessionManager.getUsername()
    val token = sessionManager.getToken()

    var tareas by rememberSaveable { mutableStateOf<List<TareaResponseDTO>>(emptyList()) }
    var isLoading by rememberSaveable { mutableStateOf(false) }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    // Función para cargar las tareas
    fun cargarTareas() {
        scope.launch(Dispatchers.IO) {
            if (username != null && token != null) {
                isLoading = true
                errorMessage = null

                try {
                    val response = RetrofitClient.getRetrofit().obtenerTareas("Bearer $token", username)
                    if (response.isSuccessful) {
                        tareas = response.body() ?: emptyList()
                    } else {
                        errorMessage = "Error al obtener las tareas: ${response.errorBody()?.string()}"
                    }
                } catch (e: Exception) {
                    errorMessage = "Error en la red: ${e.localizedMessage}"
                } finally {
                    isLoading = false
                }
            } else {
                errorMessage = "No se encontró el nombre de usuario"
            }
        }
    }

    // Cargar las tareas al iniciar la pantalla
    LaunchedEffect(Unit) {
        cargarTareas()
    }

    // Función para borrar una tarea
    fun borrarTarea(titulo: String) {
        scope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.getRetrofit().borrarTarea("Bearer $token", titulo)
                if (response.isSuccessful) {
                    // Recargar la lista de tareas después de borrar
                    cargarTareas()
                } else {
                    errorMessage = "Error al borrar la tarea: ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error en la red: ${e.localizedMessage}"
            }
        }
    }

    // Función para cambiar el estado de una tarea
    fun cambiarEstadoTarea(titulo: String, nuevoEstado: EstadoTarea) {
        scope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.getRetrofit().cambiarEstado("Bearer $token", titulo, nuevoEstado)
                if (response.isSuccessful) {
                    // Recargar la lista de tareas después de cambiar el estado
                    cargarTareas()
                } else {
                    errorMessage = "Error al cambiar el estado de la tarea: ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error en la red: ${e.localizedMessage}"
            }
        }
    }

    // Mostrar la interfaz de usuario
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else if (errorMessage != null) {
            Text(text = errorMessage!!, color = Color.Red)
        } else {
            if (tareas.isEmpty()) {
                Text("No hay tareas disponibles")
            } else {
                ListaDeTareas(
                    tareas = tareas,
                    onDeleteTarea = { titulo -> borrarTarea(titulo) }, // Pasar la función de borrado
                    onToggleComplete = { titulo, nuevoEstado ->
                        cambiarEstadoTarea(titulo, nuevoEstado) // Pasar el título y el nuevo estado
                    }
                )
            }
        }

        MyButton("Registrar tarea", 30) { navController.navigate("registroTarea") }

        Button(onClick = {
            sessionManager.clearSession()
            navController.navigate("login") {
                popUpTo("myTasks") { inclusive = true }
            }
        }) {
            Text("Cerrar sesión")
        }
    }
}


@Composable
fun ListaDeTareas(
    tareas: List<TareaResponseDTO>,
    onDeleteTarea: (String) -> Unit, // Función para manejar la eliminación
    onToggleComplete: (String, EstadoTarea) -> Unit // Función para manejar el cambio de estado
) {
    LazyColumn {
        items(tareas) { tarea ->
            TareaItem(
                tarea = tarea,
                onDelete = { onDeleteTarea(tarea.titulo) }, // Pasar el título de la tarea
                onToggleComplete = { nuevoEstado ->
                    onToggleComplete(tarea.titulo, nuevoEstado) // Pasar el título y el nuevo estado
                }
            )
        }
    }
}

@Composable
fun TareaItem(
    tarea: TareaResponseDTO,
    onDelete: () -> Unit, // Función para manejar la eliminación
    onToggleComplete: (EstadoTarea) -> Unit // Función para manejar el cambio de estado
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(8.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Checkbox para marcar la tarea como completada
            Checkbox(
                checked = tarea.estado == EstadoTarea.COMPLETADA, // Estado actual de la tarea
                onCheckedChange = { isChecked ->
                    // Cambiar el estado según el valor del Checkbox
                    val nuevoEstado = if (isChecked) EstadoTarea.COMPLETADA else EstadoTarea.PENDIENTE
                    onToggleComplete(nuevoEstado)
                }
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = tarea.titulo,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = tarea.descripcion,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = tarea.estado.toString(), // Mostrar el estado como texto
                    color = if (tarea.estado == EstadoTarea.COMPLETADA) Color.Green else Color.Red,
                    fontSize = 14.sp
                )
            }

            // Icono de papelera
            IconButton(
                onClick = onDelete // Llama a la función onDelete cuando se hace clic
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Borrar tarea",
                    tint = Color.Red
                )
            }
        }
    }
}