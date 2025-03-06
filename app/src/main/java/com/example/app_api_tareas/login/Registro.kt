package com.example.app_api_tareas.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.app_api_tareas.model.UsuarioRequest
import com.example.app_api_tareas.model.UsuarioResponse
import com.example.app_api_tareas.retrofit.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun Registro(modifier: Modifier, navController: NavController) {
    var textUsername by rememberSaveable { mutableStateOf("") }
    var textPassword by rememberSaveable { mutableStateOf("") }
    var textPasswordRepeat by rememberSaveable { mutableStateOf("") }
    var textEmail by rememberSaveable { mutableStateOf("") }
    var textTelefono by rememberSaveable { mutableStateOf("") }
    var textCalle by rememberSaveable { mutableStateOf("") }
    var textNum by rememberSaveable { mutableStateOf("") }
    var textProvincia by rememberSaveable { mutableStateOf("") }
    var textMunicipio by rememberSaveable { mutableStateOf("") }
    var textCp by rememberSaveable { mutableStateOf("") }

    var usuarioBody by rememberSaveable { mutableStateOf("") }

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

        MyOutlinedText(textUsername, "Usuario") { textUsername = it}
        MyOutlinedText(textPassword, "Password") { textPassword = it}
        MyOutlinedText(textPasswordRepeat, "Repeat password") { textPasswordRepeat = it}
        MyOutlinedText(textEmail, "Email") { textEmail = it}
        MyOutlinedText(textTelefono, "Teléfono") { textTelefono = it}
        MyOutlinedText(textCalle, "Calle") { textCalle = it}
        MyOutlinedText(textNum, "Número") { textNum = it}
        MyOutlinedText(textProvincia, "Provincia") { textProvincia = it}
        MyOutlinedText(textMunicipio, "Municipio") { textMunicipio = it}
        MyOutlinedText(textCp, "CP") { textCp = it}

        Button(
            onClick = {
                scope.launch(Dispatchers.IO) {
                    try {
                        val response = RetrofitClient
                            .getRetrofit()
                            .register(
                                UsuarioRequest(
                                    textUsername,
                                    textEmail,
                                    textPassword,
                                    textPasswordRepeat,
                                    textTelefono,
                                    textCalle,
                                    textNum,
                                    textProvincia,
                                    textMunicipio,
                                    textCp
                                )
                            )
                        if (response != null) {
                            if (response.isSuccessful) {
                                usuarioBody = response.body().toString()
                                errorCode = response.code().toString()
                                resultadoRespuesta = true
                            } else {
                                errorCode = response.code().toString()
                                errorBody = response.errorBody()?.string() ?: "Error desconocido"
                                resultadoRespuesta = false
                            }
                        }
                    } catch (e: Exception) {
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

        MyButton("Atrás", 30) { navController.navigate("login") }

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
                            Text("Mensaje: $usuarioBody")
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

@Composable
fun MyOutlinedText(text: String,label: String, onValueChanged: (String) -> Unit) {
    OutlinedTextField(
        modifier = Modifier
            .width(310.dp),
        value = text,
        onValueChange = onValueChanged,
        label = { Text(label) },
        singleLine = true
    )
}