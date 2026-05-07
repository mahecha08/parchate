package com.universidad.parchate.ui.screens.Profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.auth
import com.universidad.parchate.R
import com.universidad.parchate.ui.components.cajasTexto
import com.universidad.parchate.ui.components.glowButton

@Composable
fun ChangePasswordScreen(
    onNavigateBack: () -> Unit
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    val auth = Firebase.auth
    val scrollState = rememberScrollState()

    // FIX 4: plain vals, no need for remember { mutableStateOf(...) }
    val ingresaContrasenaActual = stringResource(R.string.password_ingresa_actual)
    val ingresaNuevaContrasena = stringResource(R.string.password_ingresa_nueva)
    val ingresaConfirmarContrasena = stringResource(R.string.password_ingresa_confirmar)
    val contraseñasNoCoinciden = stringResource(R.string.password_no_coinciden)
    val contraseñaActualIncorrecta = stringResource(R.string.password_actual_incorrecta)
    val errorUsuarioNoAutenticado = stringResource(R.string.error_usuario_no_autenticado)
    val passwordCambiadoExitoso = stringResource(R.string.password_cambiado_exitoso)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Transparent) // FIX 2 & 3
    ) {
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .padding(16.dp)
                .statusBarsPadding()
        ) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = stringResource(R.string.atras),
                tint = Color.White // FIX 2
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.password_titulo),
                color = Color.White, // FIX 2
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(24.dp))

            cajasTexto(
                value = currentPassword,
                onValueChange = { currentPassword = it },
                label = stringResource(R.string.password_actual_label),
                leadingIcon = Icons.Default.Lock,
            )
            Spacer(modifier = Modifier.height(16.dp))

            cajasTexto(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = stringResource(R.string.password_nueva_label),
                leadingIcon = Icons.Default.Lock,
            )
            Spacer(modifier = Modifier.height(16.dp))

            cajasTexto(
                value = confirmNewPassword,
                onValueChange = { confirmNewPassword = it },
                label = stringResource(R.string.password_confirmar_label),
                leadingIcon = Icons.Default.Lock,
            )
            Spacer(modifier = Modifier.height(24.dp))

            if (message.isNotEmpty()) {
                Text(
                    text = message,
                    color = if (isError) Color.Red else Color.Green,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
            }

            if (!isLoading) {
                glowButton(
                    text = stringResource(R.string.password_cambiar),
                    onClick = {
                        when {
                            currentPassword.isBlank() -> {
                                message = ingresaContrasenaActual; isError = true
                            }
                            newPassword.isBlank() -> {
                                message = ingresaNuevaContrasena; isError = true
                            }
                            confirmNewPassword.isBlank() -> {
                                message = ingresaConfirmarContrasena; isError = true
                            }
                            newPassword != confirmNewPassword -> {
                                message = contraseñasNoCoinciden; isError = true
                            }
                            else -> {
                                isLoading = true
                                val user = auth.currentUser
                                if (user != null && user.email != null) {
                                    // FIX 1: re-authenticate first, then update password
                                    val credential = EmailAuthProvider
                                        .getCredential(user.email!!, currentPassword)

                                    user.reauthenticate(credential)
                                        .addOnSuccessListener {
                                            user.updatePassword(newPassword)
                                                .addOnSuccessListener {
                                                    message = passwordCambiadoExitoso
                                                    isError = false
                                                    isLoading = false
                                                }
                                                .addOnFailureListener { e ->
                                                    message = "Error: ${e.localizedMessage}"
                                                    isError = true
                                                    isLoading = false
                                                }
                                        }
                                        .addOnFailureListener {
                                            // Wrong current password
                                            message = contraseñaActualIncorrecta
                                            isError = true
                                            isLoading = false
                                        }
                                } else {
                                    message = errorUsuarioNoAutenticado
                                    isError = true
                                    isLoading = false
                                }
                            }
                        }
                    }
                )
            } else {
                CircularProgressIndicator(color = Color.White)
            }
        }
    }
}