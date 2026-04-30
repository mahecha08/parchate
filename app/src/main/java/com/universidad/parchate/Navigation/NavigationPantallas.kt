package com.universidad.parchate.Navigation

import androidx.compose.runtime.Composable
import androidx.navigation.toRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.universidad.parchate.ui.screens.Event.MyEventsScreen
import com.universidad.parchate.ui.screens.Event.UpdateEventScreen
import com.universidad.parchate.ui.screens.Favorites.FavoritesScreen
import com.universidad.parchate.ui.screens.Home.HomeScreen
import com.universidad.parchate.ui.screens.Login.ForgotPasswordScreen
import com.universidad.parchate.ui.screens.Login.LoginScreen
import com.universidad.parchate.ui.screens.Login.RegisterScreen
import com.universidad.parchate.ui.screens.Login.VerificationCodeScreen
import com.universidad.parchate.ui.screens.Profile.EditProfileScreen
import com.universidad.parchate.ui.screens.Profile.ProfileScreen
import com.universidad.parchate.ui.screens.chat.ChatbotScreen
import com.universidad.parchate.ui.screens.create.CreateEventScreen
import com.universidad.parchate.ui.screens.map.EventsMapScreen
import com.universidad.parchate.ui.screens.map.MapPickerScreen
import com.universidad.parchate.ui.screens.start.StartScreen

object Routes {
    const val START = "start"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT_PASSWORD = "forgot_password"
    const val VERIFICATION_CODE = "verification_code/{method}/{contact}"
    const val HOME = "home"
    const val CREATE_EVENT = "create_event"
    const val MAP_PICKER = "map_picker"
    const val EVENTS_MAP = "events_map"
    const val CHATBOT = "chatbot"
}

@Composable
fun NavigationPantallas() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.START
    ) {
        composable(Routes.START) {
            StartScreen(
                navigationToHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.START) { inclusive = true }
                    }
                },
                navigationToLogin = {
                    navController.navigate(Routes.LOGIN)
                }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                navigationToHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                navigationToRegister = {
                    navController.navigate(Routes.REGISTER)
                },
                onNavigateToback = {
                    navController.popBackStack()
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Routes.FORGOT_PASSWORD)
                }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToVerification = { method, contact ->
                    navController.navigate("verification_code/$method/$contact")
                }
            )
        }

        composable(Routes.VERIFICATION_CODE) { backStackEntry ->
            val method = backStackEntry.arguments?.getString("method").orEmpty()
            val contact = backStackEntry.arguments?.getString("contact").orEmpty()

            VerificationCodeScreen(
                method = method,
                contact = contact,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onVerified = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.START) { inclusive = false }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                onNavigateToCreate = {
                    navController.navigate(Routes.CREATE_EVENT)
                },
                onNavigateToMap = {
                    navController.navigate(Routes.EVENTS_MAP)
                },
                onNavigateToChatbot = {
                    navController.navigate(Routes.CHATBOT)
                },
                onNavigateToProfile = {
                    navController.navigate(Profile)
                },
                onNavigateToFavorites = {
                    navController.navigate(Favorites)
                }
            )
        }

        composable(Routes.CREATE_EVENT) { backStackEntry ->
            val savedStateHandle = backStackEntry.savedStateHandle

            val selectedLatitud = savedStateHandle.get<Double>("selected_latitud")
            val selectedLongitud = savedStateHandle.get<Double>("selected_longitud")
            val selectedPais = savedStateHandle.get<String>("selected_pais")
            val selectedCiudad = savedStateHandle.get<String>("selected_ciudad")
            val selectedDireccion = savedStateHandle.get<String>("selected_direccion")
            val selectedUbicacion = savedStateHandle.get<String>("selected_ubicacion")

            CreateEventScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
                onNavigateToMapPicker = {
                    navController.navigate(Routes.MAP_PICKER)
                },
                selectedLatitud = selectedLatitud,
                selectedLongitud = selectedLongitud,
                selectedPais = selectedPais,
                selectedCiudad = selectedCiudad,
                selectedDireccion = selectedDireccion,
                selectedUbicacion = selectedUbicacion
            )
        }

        composable(Routes.MAP_PICKER) {
            MapPickerScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onLocationSelected = { latitud, longitud, pais, ciudad, direccion, ubicacion ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("selected_latitud", latitud)

                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("selected_longitud", longitud)

                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("selected_pais", pais)

                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("selected_ciudad", ciudad)

                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("selected_direccion", direccion)

                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("selected_ubicacion", ubicacion)

                    navController.popBackStack()
                }
            )
        }

        composable(Routes.EVENTS_MAP) {
            EventsMapScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.CHATBOT) {
            ChatbotScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<Profile> {
            ProfileScreen(
                onNavigateToHome = {
                    navController.popBackStack()
                },
                onNavitageToEdit = {
                    navController.navigate(EditProfile)
                },
                onNavigateToEvents = {
                    navController.navigate(ViewMyEvents)
                },
                onNavigateToChangePassword = { },
                onNavigateToStart = {
                    val startDestinationId = navController.graph.startDestinationId
                    navController.navigate(Routes.START) {
                        popUpTo(startDestinationId) { inclusive = true }
                    }
                }
            )
        }

        composable<EditProfile> {
            EditProfileScreen(
                OnNavigateToProfile = {
                    navController.popBackStack()
                }
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

        composable<Favorites> {
            FavoritesScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetail = { }
            )
        }
    }
}
