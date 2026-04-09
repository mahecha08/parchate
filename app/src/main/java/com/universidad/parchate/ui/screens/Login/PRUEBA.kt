package com.universidad.parchate.ui.screens.Login

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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.universidad.parchate.ui.components.cajasTexto
import com.universidad.parchate.ui.components.glowButton
import com.universidad.parchate.ui.theme.BackgroundPrincipal
import com.universidad.parchate.ui.theme.RosadoNeon
import com.universidad.parchate.ui.theme.TextoSecundario

@Composable
fun LoginContent(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onTogglePasswordVisibility: () -> Unit,
    loginError: String,
    onLoginClick: (String, String, () -> Unit) -> Unit,
    navigationToHome: () -> Unit,
    navigationToRegister: () -> Unit
) {
    val scrollState = rememberScrollState()

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
                    onValueChange = onEmailChange,
                    label = "Correo Electrónico",
                    leadingIcon = Icons.Default.Email,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )


                cajasTexto(
                    value = password,
                    onValueChange = onPasswordChange,
                    label = "Contraseña",
                    leadingIcon = Icons.Default.Lock,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = onTogglePasswordVisibility) {
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
                    onClick = {
                        onLoginClick(email, password, navigationToHome)
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

@Preview(showBackground = true)
@Composable
fun LoginContentPreview() {
    LoginContent(
        email = "test@example.com",
        onEmailChange = {},
        password = "password123",
        onPasswordChange = {},
        passwordVisible = false,
        onTogglePasswordVisibility = {},
        loginError = "",
        onLoginClick = { _, _, callback -> callback() },
        navigationToHome = {},
        navigationToRegister = {}
    )
}
