package com.universidad.parchate.ui.screens.Login

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.universidad.parchate.ui.components.glowButton
import com.universidad.parchate.ui.theme.BackgroundPrincipal
import com.universidad.parchate.ui.theme.RosadoNeon
import com.universidad.parchate.ui.theme.TextoSecundario
import java.util.concurrent.TimeUnit

/**
 * Pantalla de verificación de código OTP / confirmación de correo.
 *
 * @param method    "email" o "phone"
 * @param contact   correo o número de teléfono
 * @param onNavigateBack  lambda para volver
 * @param onVerified      lambda al verificar con éxito
 */
@Composable
fun VerificationCodeScreen(
    method: String,
    contact: String,
    onNavigateBack: () -> Unit = {},
    onVerified: () -> Unit = {}
) {
    val isEmail = method == "email"
    val context = LocalContext.current
    val activity = context as Activity
    val auth = FirebaseAuth.getInstance()
    val scrollState = rememberScrollState()

    var code by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    // Estado para flujo SMS
    var verificationId by remember { mutableStateOf("") }
    var smsSent by remember { mutableStateOf(false) }
    var resendToken by remember { mutableStateOf<PhoneAuthProvider.ForceResendingToken?>(null) }

    val phoneCallbacks = remember {
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                isLoading = true
                auth.signInWithCredential(credential)
                    .addOnSuccessListener { isLoading = false; onVerified() }
                    .addOnFailureListener {
                        isLoading = false
                        isError = true
                        message = "Error en la verificación automática"
                    }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                isLoading = false
                isError = true
                message = "Error al enviar SMS: ${e.localizedMessage ?: "Inténtalo de nuevo"}"
            }

            override fun onCodeSent(vId: String, token: PhoneAuthProvider.ForceResendingToken) {
                verificationId = vId
                resendToken = token
                smsSent = true
                isLoading = false
                isError = false
                message = "Código enviado a $contact"
            }
        }
    }

    // Enviar SMS automáticamente al entrar (solo para teléfono)
    LaunchedEffect(Unit) {
        if (!isEmail) {
            isLoading = true
            PhoneAuthProvider.verifyPhoneNumber(
                PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber(contact)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(activity)
                    .setCallbacks(phoneCallbacks)
                    .build()
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrincipal)
    ) {
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .statusBarsPadding()
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Volver",
                tint = RosadoNeon
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
            Spacer(modifier = Modifier.height(64.dp))

            Icon(
                imageVector = if (isEmail) Icons.Default.Email else Icons.Default.Phone,
                contentDescription = null,
                tint = RosadoNeon,
                modifier = Modifier.size(56.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (isEmail) "Revisa tu correo" else "Verifica tu número",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (isEmail)
                    "Hemos enviado un enlace de restablecimiento a:"
                else
                    "Ingresa el código de 6 dígitos enviado a:",
                color = TextoSecundario,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = contact,
                color = RosadoNeon,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (isEmail) {
                // --- FLUJO EMAIL ---
                Text(
                    text = "Abre el enlace en el correo para crear tu nueva contraseña. Luego vuelve aquí para iniciar sesión.",
                    color = TextoSecundario,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                InfoCard(
                    items = listOf(
                        "Revisa también tu carpeta de spam",
                        "El enlace expira en 1 hora",
                        "Solo puedes usar el enlace una vez"
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                if (message.isNotEmpty()) {
                    Text(
                        text = message,
                        color = if (isError) Color(0xFFDFCB7A) else Color(0xFF4CAF50),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    )
                }

                glowButton(
                    text = "YA LO HICE – INICIAR SESIÓN",
                    onClick = onVerified
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (isLoading) {
                    CircularProgressIndicator(color = RosadoNeon, modifier = Modifier.size(32.dp))
                } else {
                    TextButton(onClick = {
                        isLoading = true
                        message = ""
                        auth.sendPasswordResetEmail(contact)
                            .addOnSuccessListener {
                                isLoading = false
                                isError = false
                                message = "✓ Correo reenviado a $contact"
                            }
                            .addOnFailureListener {
                                isLoading = false
                                isError = true
                                message = "Error al reenviar. Inténtalo más tarde."
                            }
                    }) {
                        Text("¿No llegó el correo? Reenviar", color = RosadoNeon, fontSize = 14.sp)
                    }
                }

            } else {
                // --- FLUJO TELÉFONO / OTP ---
                OtpInputField(code = code, onCodeChange = { newCode ->
                    if (newCode.length <= 6 && newCode.all { it.isDigit() }) code = newCode
                })

                Spacer(modifier = Modifier.height(24.dp))

                if (message.isNotEmpty()) {
                    Text(
                        text = message,
                        color = if (isError) Color(0xFFDFCB7A) else Color(0xFF4CAF50),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (isLoading) {
                    CircularProgressIndicator(color = RosadoNeon)
                } else {
                    glowButton(
                        text = "VERIFICAR CÓDIGO",
                        onClick = {
                            when {
                                !smsSent -> {
                                    message = "Espera mientras se envía el SMS..."
                                    isError = true
                                }
                                code.length < 6 -> {
                                    message = "Ingresa los 6 dígitos del código"
                                    isError = true
                                }
                                verificationId.isEmpty() -> {
                                    message = "Error: no se recibió el ID de verificación"
                                    isError = true
                                }
                                else -> {
                                    isLoading = true
                                    message = ""
                                    val credential = PhoneAuthProvider.getCredential(verificationId, code)
                                    auth.signInWithCredential(credential)
                                        .addOnSuccessListener { isLoading = false; onVerified() }
                                        .addOnFailureListener { e ->
                                            isLoading = false
                                            isError = true
                                            message = when {
                                                e.message?.contains("invalid") == true ->
                                                    "Código incorrecto. Inténtalo de nuevo."
                                                e.message?.contains("expired") == true ->
                                                    "El código ha expirado. Reenvíalo."
                                                else -> "Error al verificar. Inténtalo de nuevo."
                                            }
                                        }
                                }
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(onClick = {
                        isLoading = true
                        message = ""
                        code = ""
                        PhoneAuthProvider.verifyPhoneNumber(
                            PhoneAuthOptions.newBuilder(auth)
                                .setPhoneNumber(contact)
                                .setTimeout(60L, TimeUnit.SECONDS)
                                .setActivity(activity)
                                .setCallbacks(phoneCallbacks)
                                .apply { resendToken?.let { setForceResendingToken(it) } }
                                .build()
                        )
                    }) {
                        Text("¿No llegó el SMS? Reenviar", color = RosadoNeon, fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun OtpInputField(code: String, onCodeChange: (String) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Campo invisible que captura el teclado
        OutlinedTextField(
            value = code,
            onValueChange = onCodeChange,
            modifier = Modifier.size(1.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            )
        )
        // Cajas visuales
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            repeat(6) { index ->
                val digit = code.getOrNull(index)?.toString() ?: ""
                val isCurrent = index == code.length
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (digit.isNotEmpty()) RosadoNeon.copy(alpha = 0.15f)
                            else Color(0xFF2A2845)
                        )
                        .border(
                            width = if (isCurrent) 2.dp else 1.dp,
                            color = when {
                                isCurrent -> RosadoNeon
                                digit.isNotEmpty() -> RosadoNeon.copy(alpha = 0.6f)
                                else -> RosadoNeon.copy(alpha = 0.25f)
                            },
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = digit,
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoCard(items: List<String>) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = RosadoNeon.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, RosadoNeon.copy(alpha = 0.25f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items.forEach { item ->
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("•", color = RosadoNeon, fontSize = 14.sp)
                    Text(item, color = TextoSecundario, fontSize = 13.sp, lineHeight = 18.sp)
                }
            }
        }
    }
}