package com.universidad.parchate.ui.screens.Login

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.auth
import com.universidad.parchate.R
import com.universidad.parchate.ui.components.cajasTexto
import com.universidad.parchate.ui.components.glowButton
import com.universidad.parchate.ui.theme.BackgroundPrincipal
import com.universidad.parchate.ui.theme.RosadoNeon
import com.universidad.parchate.ui.theme.TextoSecundario

@Composable
fun LoginScreen(
    navigationToRegister: () -> Unit = {},
    navigationToHome: () -> Unit = {},
    onNavigateToback: () -> Unit = {},
    onNavigateToForgotPassword: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var loginError by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val auth = Firebase.auth
    val activity = LocalView.current.context as Activity

    // Capturar strings antes de callbacks
    val errorEmailNoVerificado = stringResource(R.string.login_errorEmailNoVerificado)
    val errorCredenciales = stringResource(R.string.login_errorCredenciales)
    val errorNoRegistrado = stringResource(R.string.login_errorNoRegistrado)
    val errorConexion = stringResource(R.string.login_errorConexion)
    val errorCamposIncompletos = stringResource(R.string.login_errorCamposIncompletos)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrincipal)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = stringResource(R.string.login_header),
                color = RosadoNeon,
                fontSize = 40.sp,
                modifier = Modifier.padding(vertical = 32.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Email Input
                cajasTexto(
                    value = email,
                    onValueChange = { email = it },
                    label = stringResource(R.string.login_correo),
                    leadingIcon = Icons.Default.Email,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                // Password Input
                cajasTexto(
                    value = password,
                    onValueChange = { password = it },
                    label = stringResource(R.string.login_contraseña),
                    leadingIcon = Icons.Default.Lock,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = null, tint = TextoSecundario)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Error Message Display
                if (loginError.isNotEmpty()) {
                    Text(
                        text = loginError,
                        textAlign = TextAlign.Center,
                        color = Color(0xFFDFCB7A), // Color ámbar/amarillo para avisos
                        fontSize = 14.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                }

                // Login Button with Loading Logic
                if (isLoading) {
                    CircularProgressIndicator(color = RosadoNeon)
                } else {
                    glowButton(
                        text = stringResource(R.string.login_loginbutton),
                        onClick = {
                            if (email.isNotBlank() && password.isNotBlank()) {
                                isLoading = true
                                loginError = ""

                                auth.signInWithEmailAndPassword(email.trim(), password.trim())
                                    .addOnCompleteListener(activity) { task ->
                                        isLoading = false
                                        if (task.isSuccessful) {
                                            val user = auth.currentUser
                                            // VALIDACIÓN: Solo entra si el correo está verificado
                                            if (user != null && user.isEmailVerified) {
                                                navigationToHome()
                                            } else {
                                                loginError = errorEmailNoVerificado
                                                auth.signOut()
                                            }
                                        } else {
                                            loginError = when (task.exception) {
                                                is FirebaseAuthInvalidCredentialsException -> errorCredenciales
                                                is FirebaseAuthInvalidUserException -> errorNoRegistrado
                                                else -> errorConexion
                                            }
                                        }
                                    }
                            } else {
                                loginError = errorCamposIncompletos
                            }
                        }
                    )
                }

                // Bottom Navigation Options
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(onClick = onNavigateToForgotPassword) {
                        Text(
                            text = stringResource(R.string.login_olvidarpass),
                            color = TextoSecundario,
                            fontSize = 14.sp
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 8.dp,).padding(horizontal = 6.dp)
                    ) {
                        Text(stringResource(R.string.login_nocuenta), color = TextoSecundario, fontSize = 14.sp)
                        TextButton(
                            onClick = { navigationToRegister() },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(stringResource(R.string.login_registrate), color = RosadoNeon, fontSize = 14.sp)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}