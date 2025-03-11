package com.example.app_api_tareas.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.app_api_tareas.screen.Login
import com.example.app_api_tareas.screen.MisTareas
import com.example.app_api_tareas.screen.Registro
import com.example.app_api_tareas.screen.RegistroTarea

@Composable
fun AppNavigation(modifier: Modifier) {
    val navControlador = rememberNavController()
    NavHost(navController = navControlador, startDestination = "login") {
        composable("login") {
            Login(modifier, navControlador)
        }
        composable("registro")
        {
            Registro(modifier, navControlador)
        }
        composable("misTareas")
        {
            MisTareas(modifier, navControlador)
        }
        composable("registroTarea")
        {
            RegistroTarea(modifier, navControlador)
        }

    }
}