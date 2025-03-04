package com.example.app_api_tareas.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.app_api_tareas.model.TareaRequest
import com.example.app_api_tareas.model.TareaResponse
import com.example.app_api_tareas.retrofit.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun RegistroTarea(modifier: Modifier, navController: NavController) {
    var textTitulo by rememberSaveable { mutableStateOf("") }
    var textDescripcion by rememberSaveable { mutableStateOf("") }

    var tareaResponse by rememberSaveable { mutableStateOf<TareaResponse?>(null) }

    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val username = sessionManager.getUsername()
    val token = sessionManager.getToken()

    var errorBody by rememberSaveable { mutableStateOf("") }
    var errorCode by rememberSaveable { mutableStateOf("") }

    var openDialog by rememberSaveable { mutableStateOf(false) }
    var resultadoRespuesta by rememberSaveable { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text="REGISTRO",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        MyOutlinedText(textTitulo, "Usuario") { textTitulo = it}
        MyOutlinedText(textDescripcion, "Password") { textDescripcion = it}

        Button(
            onClick = {
                scope.launch(Dispatchers.IO) {
                    try {
                        val response = username?.let {
                            TareaRequest(
                                it,
                                textTitulo,
                                textDescripcion
                            )
                        }?.let {
                            RetrofitClient
                                .getRetrofit()
                                .registerTarea("Bearer $token",
                                    it
                                )
                        }
                        if (response != null) {
                            if (response.isSuccessful) {
                                tareaResponse = response.body()?.let {
                                    TareaResponse(
                                        it._id,
                                        it.username,
                                        it.titulo,
                                        it.descripcion,
                                        it.estado,
                                        it.fecha_created
                                    )
                                }
                                errorCode = response.code().toString()
                                resultadoRespuesta = true
                            } else {
                                errorCode = response.code().toString()
                                errorBody = response.errorBody()?.string() ?: "Error desconocido"
                                resultadoRespuesta = false
                            }
                        }
                    } catch (e: Exception) {
                        tareaResponse = null
                        errorBody = "Error en la red: ${e.localizedMessage}"
                    }
                    openDialog = true
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.Black
            ),
            contentPadding = PaddingValues(horizontal = 30.dp, vertical = 20.dp),
            border = BorderStroke(1.dp, Color.Black)
        ) {
            Text(
                text = "Registrar",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (openDialog) {
            AlertDialog(
                onDismissRequest = {
                    openDialog = false
                },
                title = {
                    Text(text = "Registro")
                },
                text = {
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Code: $errorCode\n")
                        if (resultadoRespuesta) {
                            Text("Mensaje: $tareaResponse")
                        } else {
                            Text("Mensaje: $errorBody")
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {

                        openDialog = false
                    }) {
                        Text("Aceptar")
                    }
                }
            )
        }

    }
}
