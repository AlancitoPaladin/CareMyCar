package com.itsm.caremycar.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.itsm.caremycar.screens.user.AddVehicle
import com.itsm.caremycar.screens.user.CarDetailsScreen
import com.itsm.caremycar.screens.user.ProductDetailsScreen
import com.itsm.caremycar.screens.user.UserScreen
import com.itsm.caremycar.screens.agency.*
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

        composable("user_screen") { backStackEntry ->
            val vehicleCreated by backStackEntry.savedStateHandle
                .getStateFlow("vehicle_created", false)
                .collectAsState()
            UserScreen(
                onAddVehicleClick = { navController.navigate("add_vehicle") },
                onVehicleClick = { vehicleId ->
                    navController.navigate("car_details/$vehicleId")
                },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                },
                shouldRefreshOnResume = vehicleCreated,
                onRefreshHandled = {
                    backStackEntry.savedStateHandle["vehicle_created"] = false
                }
            )
        }

        composable("add_vehicle") {
            AddVehicle(
                onBack = { navController.popBackStack() },
                onVehicleCreated = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("vehicle_created", true)
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "car_details/{vehicleId}",
            arguments = listOf(navArgument("vehicleId") { type = NavType.StringType })
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getString("vehicleId").orEmpty()
            CarDetailsScreen(
                vehicleId = vehicleId,
                onBack = { navController.popBackStack() }
            )
        }

        composable("product_details") {
            ProductDetailsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // --- SECCIÓN AGENCY ---

        composable("admin_screen") {
            MenuAgency(
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                },
                onNavigateToCatalog = { navController.navigate("agency_catalog") },
                onNavigateToOrders = { navController.navigate("agency_orders") },
                onNavigateToReminders = { navController.navigate("agency_reminders") }
            )
        }

        composable("agency_catalog") {
            CatalogoRefaccionesScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddParts = { navController.navigate("agency_add_parts") },
                onNavigateToEditPart = { partId -> navController.navigate("agency_edit_part/$partId") }
            )
        }

        composable("agency_add_parts") {
            AddPartsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "agency_edit_part/{partId}",
            arguments = listOf(navArgument("partId") { type = NavType.StringType })
        ) { backStackEntry ->
            val partId = backStackEntry.arguments?.getString("partId").orEmpty()
            EditPartScreen(
                partId = partId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("agency_orders") {
            PedidosScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddOrder = { navController.navigate("agency_add_order") }
            )
        }

        composable("agency_add_order") {
            AddOrderScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("agency_reminders") {
            RemindersScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
