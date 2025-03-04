package com.example.app_api_tareas.model

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val token: String
)
