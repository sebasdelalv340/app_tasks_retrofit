package com.example.app_api_tareas.model

data class UsuarioRequest(
    val username: String,
    val email: String,
    val password: String,
    val passwordRepeat: String,
    val telefono: String,
    val calle: String,
    val num: String,
    val provincia: String,
    val municipio: String,
    val cp: String
)

data class UsuarioResponse(
    val _id : String?,
    val username: String,
    var password: String,
    val email: String,
    val telefono: String,
    val direccion: Direccion?,
    val roles: String? = "USER"
) {
    override fun toString(): String {
        return "Usuario\n" +
                "id: $_id\nusername: $username\npassword: $password\nemail: $email\ntelefono: $telefono\ndirección: $direccion\nrol: $roles"
    }
}

data class Direccion(
    val calle: String,
    val num: String,
    val provincia: String,
    val municipio: String,
    val cp: String
)
