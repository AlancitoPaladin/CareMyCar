package com.itsm.caremycar.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.itsm.caremycar.screens.agency.UserScreen
import com.itsm.caremycar.session.Login
import com.itsm.caremycar.session.LoginViewModel
import com.itsm.caremycar.session.Register

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            val viewModel: LoginViewModel = hiltViewModel()
            Login(
                viewModel = viewModel,
                onNavigateToRegister = { navController.navigate("register") },
                onNavigateToHome = { role ->
                    val destination = if (role == "admin") "admin_screen" else "user_screen"
                    navController.navigate(destination) {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("register") {
            Register(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onNavigateToHome = {
                    navController.navigate("user_screen") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            )
        }

        composable("user_screen") {
            UserScreen()
        }

        composable("admin_screen") {
            // AdminScreen() // ← cuando lo tengas
        }
    }
}