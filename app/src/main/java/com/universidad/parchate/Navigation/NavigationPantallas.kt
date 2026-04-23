package com.universidad.parchate.Navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.universidad.parchate.ui.screens.Event.MyEventsScreen
import com.universidad.parchate.ui.screens.Event.UpdateEventScreen
import com.universidad.parchate.ui.screens.Home.HomeScreen
import com.universidad.parchate.ui.screens.Login.ForgotPasswordScreen
import com.universidad.parchate.ui.screens.Login.LoginScreen
import com.universidad.parchate.ui.screens.Login.RegisterScreen
import com.universidad.parchate.ui.screens.Login.VerificationCodeScreen
import com.universidad.parchate.ui.screens.Profile.ProfileScreen
import com.universidad.parchate.ui.screens.Profile.EditProfileScreen
import com.universidad.parchate.ui.screens.create.CreateEventScreen
import com.universidad.parchate.ui.screens.start.StartScreen

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
                navigationToHome = {
                    navController.navigate(Home) {
                        popUpTo(Login) { inclusive = true }
                    }
                },
                navigationToRegister = { navController.navigate(Register) },
                onNavigateToback = {
                    navController.navigate(Login) {
                        popUpTo(Login) { inclusive = true }
                    }
                },
                // Nuevo: navegar a olvidé contraseña
                onNavigateToForgotPassword = { navController.navigate(ForgotPassword) }
            )
        }

        composable<Home> {
            HomeScreen(
                onNavigateToCreate = {
                    navController.navigate(CreateEvent)
                },
                onNavigateToProfile = {
                    navController.navigate(Profile)
                }
            )
        }

        composable<Register> {
            RegisterScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToLogin = {
                    navController.navigate(Login) {
                        popUpTo(Login) { inclusive = true }
                    }
                }
            )
        }

        composable<ForgotPassword> {
            ForgotPasswordScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToVerification = { method, contact ->
                    navController.navigate(VerificationCode(method = method, contact = contact))
                }
            )
        }

        composable<VerificationCode> { backStackEntry ->
            val args = backStackEntry.toRoute<VerificationCode>()
            VerificationCodeScreen(
                method = args.method,
                contact = args.contact,
                onNavigateBack = { navController.popBackStack() },
                onVerified = {
                    navController.navigate(Login) {
                        popUpTo(Inicio) { inclusive = false }
                    }
                }
            )
        }
        composable<CreateEvent> {
            CreateEventScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHome = {
                    navController.navigate(Home) {
                        popUpTo(Home) { inclusive = true }
                    }
                }
            )
        }

        composable<Profile> {
            ProfileScreen(
                onNavigateToHome = {
                    navController.navigate(Home) {
                        popUpTo(Home) { inclusive = true }
                    }
                },
                onNavitageToEdit = { navController.navigate(EditProfile) },
                onNavigateToEvents = { navController.navigate(ViewMyEvents) },
                onNavigateToChangePassword = { },
                onNavigateToStart = {
                    navController.navigate(Inicio) {
                        popUpTo(Inicio) { inclusive = true }
                    }
                }
            )
        }

        composable<EditProfile> {
            EditProfileScreen(
                OnNavigateToProfile = {navController.navigate(Profile)}
            )
        }

        composable<UpdateEvent> { backStackEntry ->
            val args = backStackEntry.toRoute<UpdateEvent>()
            UpdateEventScreen(
                eventId = args.eventId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<ViewMyEvents> {
            MyEventsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { eventId ->
                    navController.navigate(UpdateEvent(eventId = eventId))
                }
            )
        }


    }
}