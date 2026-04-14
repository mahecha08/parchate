package com.universidad.parchate.ui.screens.Login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import com.universidad.parchate.R
import com.universidad.parchate.data.service.FirebaseService
import com.universidad.parchate.ui.components.cajasTexto
import com.universidad.parchate.ui.components.glowButton
import com.universidad.parchate.ui.theme.RosadoNeon
import com.universidad.parchate.ui.theme.TextoSecundario
import com.universidad.parchate.ui.theme.BackgroundPrincipal
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var nombres by remember { mutableStateOf("") }
    var cedula by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var terminosAceptados by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }

    val cumpleLongitud = password.length >= 8
    val cumpleMayuscula = password.any { it.isUpperCase() }
    val cumpleEspecial = password.any { !it.isLetterOrDigit() }
    val coinciden = password == confirmPassword && password.isNotEmpty()

    val firebaseService = remember { FirebaseService() }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // Capturar strings antes de callbacks
    val registroExito = stringResource(R.string.register_exito)
    val errorRegistro = stringResource(R.string.register_error_registro)
    val errorTerminos = stringResource(R.string.register_error_terminos)
    val errorContrasena = stringResource(R.string.register_error_contrasena)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrincipal)
            .verticalScroll(scrollState)
    ) {
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.padding(top = 40.dp, start = 8.dp)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.register_atras), tint = RosadoNeon)
        }

        Text(
            text = stringResource(R.string.register_titulo),
            color = RosadoNeon,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
        )

        cajasTexto(
            value = nombres,
            onValueChange = { nombres = it },
            label = stringResource(R.string.register_nombres),
            leadingIcon = Icons.Default.Person
        )

        Spacer(modifier = Modifier.height(12.dp))

        cajasTexto(
            value = cedula,
            onValueChange = { if (it.all { char -> char.isDigit() }) cedula = it },
            label = stringResource(R.string.register_cedula),
            leadingIcon = Icons.Default.Badge,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(12.dp))

        cajasTexto(
            value = fechaNacimiento,
            onValueChange = { fechaNacimiento = it },
            label = stringResource(R.string.register_fecha_nacimiento),
            leadingIcon = Icons.Default.DateRange,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(12.dp))

        cajasTexto(
            value = correo,
            onValueChange = { correo = it },
            label = stringResource(R.string.register_correo),
            leadingIcon = Icons.Default.Email,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(12.dp))

        cajasTexto(
            value = password,
            onValueChange = { password = it },
            label = stringResource(R.string.register_contrasena),
            leadingIcon = Icons.Default.Lock,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null, tint = TextoSecundario
                    )
                }
            }
        )

        Column(modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp)) {
            Text(stringResource(R.string.register_contrasena_requisito), color = Color.White, fontSize = 12.sp)
            ValidacionItem(stringResource(R.string.register_contrasena_minimo), cumpleLongitud)
            ValidacionItem(stringResource(R.string.register_contrasena_mayuscula), cumpleMayuscula)
            ValidacionItem(stringResource(R.string.register_contrasena_especial), cumpleEspecial)
        }

        cajasTexto(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = stringResource(R.string.register_confirmar_contrasena),
            leadingIcon = Icons.Default.Lock,
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(
                        imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null, tint = TextoSecundario
                    )
                }
            }
        )

        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = terminosAceptados,
                onCheckedChange = { terminosAceptados = it },
                colors = CheckboxDefaults.colors(checkedColor = RosadoNeon, uncheckedColor = TextoSecundario)
            )
            Text(stringResource(R.string.register_terminos), color = TextoSecundario, fontSize = 14.sp)
        }

        if (errorMessage.isNotEmpty()) {
            Text(errorMessage, color = Color.Red, fontSize = 14.sp, modifier = Modifier.padding(horizontal = 32.dp))
        }

        if (successMessage.isNotEmpty()) {
            Text(successMessage, color = Color.Green, fontSize = 14.sp, modifier = Modifier.padding(horizontal = 32.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            if (isLoading) {
                CircularProgressIndicator(color = RosadoNeon)
            } else {
                glowButton(
                    text = stringResource(R.string.register_continuar),
                    onClick = {
                        if (coinciden && cumpleLongitud && cumpleMayuscula && cumpleEspecial && terminosAceptados) {
                            isLoading = true
                            errorMessage = ""
                            scope.launch {
                                val result = firebaseService.registerUser(
                                    email = correo.trim(),
                                    password = password.trim(),
                                    nombres = nombres,
                                    cedula = cedula,
                                    fechaNacimiento = fechaNacimiento
                                )
                                isLoading = false
                                result.fold(
                                    onSuccess = {
                                        successMessage = registroExito
                                        onNavigateToLogin()
                                    },
                                    onFailure = { e ->
                                        errorMessage = e.message ?: errorRegistro
                                    }
                                )
                            }
                        } else if (!terminosAceptados) {
                            errorMessage = errorTerminos
                        } else {
                            errorMessage = errorContrasena
                        }
                    }
                )
            }
        }

        Text(
            text = stringResource(R.string.register_yacuenta),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp)
                .clickable { onNavigateToLogin() },
            textAlign = TextAlign.Center,
            fontSize = 14.sp
        )
    }
}
@Composable
fun ValidacionItem(texto: String, esValido: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Icon(
            imageVector = if (esValido) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (esValido) Color.Green else TextoSecundario,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = texto,
            color = if (esValido) Color.Green else TextoSecundario,
            fontSize = 12.sp
        )
    }
}