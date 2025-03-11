package com.example.app_api_tareas.screen

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
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.app_api_tareas.model.UsuarioRequest
import com.example.app_api_tareas.retrofit.RetrofitClient
import com.example.app_api_tareas.viewmodel.RegistroUserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun Registro(modifier: Modifier, navController: NavController) {
    val viewModel: RegistroUserViewModel = viewModel() // Obtener el ViewModel

    // Observar los estados del ViewModel
    val textUsername by viewModel.textUsername.collectAsState()
    val textPassword by viewModel.textPassword.collectAsState()
    val textPasswordRepeat by viewModel.textPasswordRepeat.collectAsState()
    val textEmail by viewModel.textEmail.collectAsState()
    val textTelefono by viewModel.textTelefono.collectAsState()
    val textCalle by viewModel.textCalle.collectAsState()
    val textNum by viewModel.textNum.collectAsState()
    val textProvincia by viewModel.textProvincia.collectAsState()
    val textMunicipio by viewModel.textMunicipio.collectAsState()
    val textCp by viewModel.textCp.collectAsState()

    val usuarioBody by viewModel.usuarioBody.collectAsState()
    val errorBody by viewModel.errorBody.collectAsState()
    val errorCode by viewModel.errorCode.collectAsState()
    val openDialog by viewModel.openDialog.collectAsState()
    val resultadoRespuesta by viewModel.resultadoRespuesta.collectAsState()

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

        MyOutlinedText(textUsername, "Usuario") { viewModel.updateUsername(it) }
        MyOutlinedText(textPassword, "Password") { viewModel.updatePassword(it) }
        MyOutlinedText(textPasswordRepeat, "Repeat password") { viewModel.updatePasswordRepeat(it) }
        MyOutlinedText(textEmail, "Email") { viewModel.updateEmail(it) }
        MyOutlinedText(textTelefono, "Teléfono") { viewModel.updateTelefono(it) }
        MyOutlinedText(textCalle, "Calle") { viewModel.updateCalle(it) }
        MyOutlinedText(textNum, "Número") { viewModel.updateNum(it) }
        MyOutlinedText(textProvincia, "Provincia") { viewModel.updateProvincia(it) }
        MyOutlinedText(textMunicipio, "Municipio") { viewModel.updateMunicipio(it) }
        MyOutlinedText(textCp, "CP") { viewModel.updateCp(it) }

        Button(
            onClick = {
                viewModel.register() // Llamar a la función de registro
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
                            Text("Mensaje: $usuarioBody")
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