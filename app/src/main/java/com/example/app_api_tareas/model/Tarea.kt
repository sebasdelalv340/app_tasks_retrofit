package com.example.app_api_tareas.model

import java.util.Date

enum class EstadoTarea {
    PENDIENTE,
    COMPLETADA
}

data class TareaRequest(
    val username: String,
    val titulo: String,
    val descripcion: String,
)

data class TareaResponse(
    val _id : String?,
    val username: String,
    var titulo: String,
    val descripcion: String,
    val estado: EstadoTarea,
    val fecha_created: Date
) {
    override fun toString(): String {
        return "Usuario\n" +
                "id: $_id\nusername: $username\npassword: $titulo\nemail: $descripcion\ntelefono: $estado\ndirección: $fecha_created"
    }
}

data class TareaResponseDTO(
    val username: String,
    var titulo: String,
    val descripcion: String,
    val estado: EstadoTarea,
    val fecha_created: Date
)
