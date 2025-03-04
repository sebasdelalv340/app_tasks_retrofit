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
import com.example.app_api_tareas.model.LoginRequest
import com.example.app_api_tareas.model.LoginResponse
import com.example.app_api_tareas.retrofit.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun Login(modifier: Modifier, navController: NavController) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    var textUser by rememberSaveable { mutableStateOf("")}
    var textPassword by rememberSaveable {mutableStateOf("")}
    var usuarioResponse by rememberSaveable { mutableStateOf<LoginResponse?>(null) }

    val scope = rememberCoroutineScope()

    var errorBody by rememberSaveable { mutableStateOf("") }
    var errorCode by rememberSaveable { mutableStateOf("") }

    var resultadoRespuesta by rememberSaveable { mutableStateOf(false) }

    var openDialog by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        Text(
            text="LOGIN",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
            )

        OutlinedTextField(
            modifier = Modifier
                .width(310.dp),
            value = textUser,
            onValueChange = { textUser = it },
            label = { Text("Usuario") },
            singleLine = true
        )

        OutlinedTextField(
            modifier = Modifier
                .width(310.dp),
            value = textPassword,
            onValueChange = {  textPassword = it },
            label = { Text("Contraseña") },
            singleLine = true
        )

        MyButton("Iniciar sesión", 30) {
            scope.launch(Dispatchers.IO) {
                try {
                    val response = RetrofitClient.getRetrofit().login(LoginRequest(textUser, textPassword))
                    if (response.isSuccessful) {
                        usuarioResponse = response.body()
                        errorCode = response.code().toString()
                        resultadoRespuesta = true

                        // Guardar el username y el token en SharedPreferences
                        usuarioResponse?.let { loginResponse ->
                            sessionManager.saveUserSession(textUser, loginResponse.token)
                        }
                        withContext(Dispatchers.Main) {
                            navController.navigate("myTasks")
                        }
                    } else {
                        errorCode = response.code().toString()
                        errorBody = response.errorBody()?.string() ?: "Error desconocido"
                        resultadoRespuesta = false
                    }
                } catch (e: Exception) {
                    usuarioResponse = null
                    errorBody = "Error en la red: ${e.localizedMessage}"
                }
                openDialog = true
            }
        }
        MyButton("Registrarme", 30) { navController.navigate("registro") }

        if (openDialog) {
            AlertDialog(
                onDismissRequest = {
                    openDialog = false
                },
                title = {
                    Text(text = "Login")
                },
                text = {
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Code: $errorCode\n")
                        if (resultadoRespuesta) {
                            Text("Token: $usuarioResponse")
                        } else {
                            Text("Token: $errorBody")
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
fun MyButton(texto: String, paddingH: Int, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.Black
        ),
        contentPadding = PaddingValues(horizontal = paddingH.dp, vertical = 20.dp),
        border = BorderStroke(1.dp, Color.Black)
    ) {
        Text(
            text = texto,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

