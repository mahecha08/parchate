package com.universidad.parchate.ui.screens.Login

import android.R
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.universidad.parchate.ui.components.cajasTexto
import com.universidad.parchate.ui.components.glowButton
import com.universidad.parchate.ui.theme.BackgroundPrincipal
import com.universidad.parchate.ui.theme.RosadoNeon
import com.universidad.parchate.ui.theme.TextoSecundario
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.auth

@Composable
fun LoginScreen(
    navigationToHome: () -> Unit = {},
    navigationToRegister: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val auth = Firebase.auth
    val activity = LocalView.current.context as Activity
    var loginError by remember { mutableStateOf("")}

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrincipal)
            .verticalScroll(scrollState)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.weight(1f))


            Text(
                text = "PARCHATE",
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

                cajasTexto(
                    value = email,
                    onValueChange = { email = it },
                    label = "Correo Electrónico",
                    leadingIcon = Icons.Default.Email,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )


                cajasTexto(
                    value = password,
                    onValueChange = { password = it },
                    label = "Contraseña",
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

                Spacer(modifier = Modifier.height(16.dp))
                if (loginError.isNotEmpty()){Text(
                    loginError,
                    textAlign = TextAlign.Center,
                    color = Color(0xFFDFCB7A),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )}

                glowButton(

                    text = "INICIAR SESIÓN",
                    onClick ={
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(activity) { task ->
                                if (task.isSuccessful){
                                    navigationToHome()
                                }else{
                                    loginError = when(task.exception){
                                        is FirebaseAuthInvalidCredentialsException -> "Verifique sus credenciales"
                                        is FirebaseAuthInvalidUserException -> "Correo no registrado"
                                        else -> "Error al iniciar sesion"
                                    }
                                }

                            }
                    }
                )


                Column(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TextButton(onClick = { /* Olvidar contraseña */ }) {
                        Text("¿Olvidaste tu contraseña?", color = TextoSecundario)
                    }


                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("¿No tienes cuenta? ", color = TextoSecundario)
                        TextButton(
                            onClick = navigationToRegister,
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Registrate", color = RosadoNeon )
                        }
                    }
                }
            }


            Spacer(modifier = Modifier.weight(1f))
        }
    }
}