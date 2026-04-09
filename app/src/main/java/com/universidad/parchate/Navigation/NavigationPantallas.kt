package com.universidad.parchate.Navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.universidad.parchate.ui.screens.Home.HomeScreen
import com.universidad.parchate.ui.screens.start.StartScreen
import com.universidad.parchate.ui.screens.Login.LoginScreen
import com.universidad.parchate.ui.screens.Registration.RegistrationScreen
import com.universidad.parchate.ui.screens.Policy.PolicyScreen
@Composable
fun NavigationPantallas() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Inicio) {
        composable<Inicio> {
            StartScreen(
                navigationToLogin = { navController.navigate(Login) }
            )
        }
        composable<Login> {
            LoginScreen(
                navigationToHome = { navController.navigate(Home){
                    popUpTo(Login){inclusive=true}
                } },
                navigationToRegister = {navController.navigate(Registration)}
            )
        }
        composable<Home> {
            HomeScreen()
        }
        composable<Registration> {
            RegistrationScreen(
                NavigationToLogin={ navController.navigate(Login)},
                NavigationToPolicy = {navController.navigate(Policy)})
        }
        composable<Policy>{
            PolicyScreen(
                NavigationToLogin = {navController.navigate(Login)},
                NavigationToVerification = {}
            )
        }
    }
}