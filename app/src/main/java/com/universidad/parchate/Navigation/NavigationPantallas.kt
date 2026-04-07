package com.universidad.parchate.Navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.universidad.parchate.ui.screens.Home.HomeScreen
import com.universidad.parchate.ui.screens.start.StartScreen
import com.universidad.parchate.ui.screens.Login.LoginScreen
import com.universidad.parchate.ui.screens.Login.RegisterScreen

@Composable
fun NavigationPantallas() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Inicio) {
        composable<Inicio> {
            StartScreen(
                navigationToLogin = { navController.navigate(Login) },

            )
        }
        composable<Login> {
            LoginScreen(
                navigationToHome = { navController.navigate(Home){
                    popUpTo(Login){inclusive=true}
                } },
                navigationToRegister = { navController.navigate(Register) },
                onNavigateToback = {
                    navController.navigate(Login) {

                        popUpTo(Login) { inclusive = true }
                    }
                }
            )
        }
        composable<Home> {
            HomeScreen()
        }
        composable<Register> {
            RegisterScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToLogin = {
                    navController.navigate(Login) {
                        popUpTo(Login) { inclusive = true }
                    }
                }
            )
        }

    }
}