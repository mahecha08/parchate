package com.universidad.parchate.ui.screens.Login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.universidad.parchate.R
import com.universidad.parchate.ui.components.cajasTexto
import com.universidad.parchate.ui.components.glowButton
import com.universidad.parchate.ui.theme.BackgroundPrincipal
import com.universidad.parchate.ui.theme.RosadoNeon
import com.universidad.parchate.ui.theme.TextoSecundario

@Composable
fun ForgotPasswordScreen(
    onNavigateBack: () -> Unit,
    onNavigateToVerification: (method: String, contact: String) -> Unit
) {
    var selectedMethod by remember { mutableStateOf(0) } // 0=none, 1=email, 2=phone
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    val auth = Firebase.auth
    val scrollState = rememberScrollState()

    // Capturar strings antes de usarlas en callbacks
    val ingresaCorreo = stringResource(R.string.forgot_ingresa_correo)
    val ingresaTelefono = stringResource(R.string.forgot_ingresa_telefono)

    Column (modifier = Modifier.fillMaxSize().background(BackgroundPrincipal)) {
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.padding(16.dp).statusBarsPadding()
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.forgot_volver), tint = RosadoNeon)
        }

        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(scrollState).padding(horizontal = 24.dp).statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "PARCHATE", color = RosadoNeon, fontSize = 36.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = stringResource(R.string.forgot_titulo), color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(32.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MethodCard(selected = selectedMethod == 1, label = stringResource(R.string.forgot_correo_metodo),
                    icon = { Icon(Icons.Default.Email, null, tint = if (selectedMethod == 1) Color.White else TextoSecundario) },
                    onClick = { selectedMethod = 1; message = "" }, modifier = Modifier.weight(1f))
                MethodCard(selected = selectedMethod == 2, label = stringResource(R.string.forgot_telefono_metodo),
                    icon = { Icon(Icons.Default.Phone, null, tint = if (selectedMethod == 2) Color.White else TextoSecundario) },
                    onClick = { selectedMethod = 2; message = "" }, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            when (selectedMethod) {
                1 -> {
                    cajasTexto(
                        value = email,
                        onValueChange = { email = it },
                        label = stringResource(R.string.forgot_correo_label),
                        leadingIcon = Icons.Default.Email,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
                }
                2 -> {
                    cajasTexto(value = phone,
                        onValueChange = { phone = it },
                        label = stringResource(R.string.forgot_telefono_label),
                        leadingIcon = Icons.Default.Phone,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (message.isNotEmpty()) {
                Text(text = message, color = if (isError) Color.Yellow else Color.Green, fontSize = 13.sp, textAlign = TextAlign.Center)
            }

            if (selectedMethod != 0) {
                Spacer(modifier = Modifier.height(16.dp))
                if (isLoading) {
                    CircularProgressIndicator(color = RosadoNeon)
                } else {
                    glowButton(
                        text = if (selectedMethod == 1) stringResource(R.string.forgot_enviar_enlace) else stringResource(R.string.forgot_enviar_codigo),
                        onClick = {
                            if (selectedMethod == 1) {
                                if (email.isBlank()) { message = ingresaCorreo; isError = true }
                                else {
                                    isLoading = true
                                    auth.sendPasswordResetEmail(email.trim())
                                        .addOnSuccessListener { onNavigateToVerification("email", email.trim()) }
                                        .addOnFailureListener { e -> message = "Error: ${e.localizedMessage}"; isError = true; isLoading = false }
                                }
                            } else {
                                if (phone.isBlank()) { message = ingresaTelefono; isError = true }
                                else { onNavigateToVerification("phone", phone.trim()) }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun MethodCard(selected: Boolean, label: String, icon: @Composable () -> Unit, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(80.dp),
        shape = RoundedCornerShape(16.dp),
        color = if (selected) RosadoNeon.copy(alpha = 0.15f) else Color.Transparent,
        border = BorderStroke(width = if (selected) 2.dp else 1.dp, color = if (selected) RosadoNeon else RosadoNeon.copy(alpha = 0.35f))
    ) {
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            icon(); Spacer(Modifier.height(6.dp))
            Text(text = label, color = if (selected) Color.White else TextoSecundario, fontSize = 13.sp)
        }
    }
}