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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.app_api_tareas.session.SessionManager
import com.example.app_api_tareas.viewmodel.LoginViewModel

@Composable
fun Login(modifier: Modifier, navController: NavController) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val viewModel: LoginViewModel = viewModel()

    // Observar los estados del ViewModel
    val textUser by viewModel.textUser.collectAsState()
    val textPassword by viewModel.textPassword.collectAsState()
    val errorCode by viewModel.errorCode.collectAsState()
    val errorBody by viewModel.errorBody.collectAsState()
    val openDialog by viewModel.openDialog.collectAsState()

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
            onValueChange = { viewModel.updateUser(it) },
            label = { Text("Usuario") },
            singleLine = true
        )

        OutlinedTextField(
            modifier = Modifier
                .width(310.dp),
            value = textPassword,
            onValueChange = {  viewModel.updatePassword(it) },
            label = { Text("Contraseña") },
            singleLine = true
        )

        MyButton("Iniciar sesión", 30) {
            viewModel.login(sessionManager) {
                navController.navigate("misTareas")
            }
        }
        MyButton("Registrarme", 30) { navController.navigate("registro") }

        if (openDialog) {
            AlertDialog(
                onDismissRequest = {
                    viewModel.closeDialog()
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
                        Text("Token: $errorBody")
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

