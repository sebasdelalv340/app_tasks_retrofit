package com.example.app_api_tareas.screen

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
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.app_api_tareas.model.TareaRequest
import com.example.app_api_tareas.retrofit.RetrofitClient
import com.example.app_api_tareas.viewmodel.RegistroTareaViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun RegistroTarea(modifier: Modifier, navController: NavController) {
    val viewModel: RegistroTareaViewModel = viewModel() // Obtener el ViewModel

    // Observar los estados del ViewModel
    val textTitulo by viewModel.textTitulo.collectAsState()
    val textDescripcion by viewModel.textDescripcion.collectAsState()
    val errorBody by viewModel.errorBody.collectAsState()
    val errorCode by viewModel.errorCode.collectAsState()
    val openDialog by viewModel.openDialog.collectAsState()
    val resultadoRespuesta by viewModel.resultadoRespuesta.collectAsState()

    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val username = sessionManager.getUsername()
    val token = sessionManager.getToken()

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

        MyOutlinedText(textTitulo, "Titulo") { viewModel.updateTitulo(it) }
        MyOutlinedText(textDescripcion, "Descripción") { viewModel.updateDescripcion(it) }

        Button(
            onClick = {
                viewModel.registerTarea(username, token) // Llamar a la función de registro
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

        MyButton("Ir a mis tareas", 30) { navController.navigate("misTareas") }

        if (openDialog) {
            AlertDialog(
                onDismissRequest = {
                    viewModel.closeDialog()
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
                            Text("Mensaje: Tarea guardada")
                        } else {
                            Text("Mensaje: $errorBody")
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        viewModel.closeDialog()
                    }) {
                        Text("Aceptar")
                    }
                }
            )
        }

    }
}
