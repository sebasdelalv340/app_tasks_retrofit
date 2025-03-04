package com.example.app_api_tareas.retrofit

import com.example.app_api_tareas.model.LoginRequest
import com.example.app_api_tareas.model.LoginResponse
import com.example.app_api_tareas.model.TareaRequest
import com.example.app_api_tareas.model.TareaResponse
import com.example.app_api_tareas.model.UsuarioRequest
import com.example.app_api_tareas.model.UsuarioResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @POST("usuarios/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("usuarios/register")
    suspend fun register(@Body request: UsuarioRequest): Response<UsuarioResponse>

    @POST("tareas/register")
    suspend fun registerTarea(@Header("Authorization") token: String,
                              @Body request: TareaRequest): Response<TareaResponse>

    @GET("tareas/{username}")
    suspend fun obtenerTareas(
        @Header("Authorization") token: String,
        @Path("username") username: String): Response<List<TareaResponse>>

    @PUT("tareas/{titulo}")
    suspend fun cambiarEstado(@Header("Authorization") token: String,
                              @Path("titulo") titulo: String): Response<TareaResponse>

    @DELETE("tareas/{titulo}")
    suspend fun borrarTarea(@Header("Authorization") token: String,
                            @Path("titulo") titulo: String): Response<Unit>


}