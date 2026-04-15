package com.universidad.parchate.ui.screens.create

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardOptions
import com.universidad.parchate.data.model.Evento
import com.universidad.parchate.ui.components.cajasTexto
import com.universidad.parchate.ui.components.glowButton
import com.universidad.parchate.ui.components.TimePickerCaja
import com.universidad.parchate.ui.theme.*

@Composable
fun CreateEventScreen(
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit
) {

    var nombre by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var hora by remember { mutableStateOf("") }
    var ubicacion by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var errorMensaje by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1B172E))
            .verticalScroll(rememberScrollState())
    ) {

        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.padding(top = 40.dp, start = 8.dp)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = null, tint = RosadoNeon)
        }

        Text(
            text = "Registra tu\nEvento",
            color = RosadoNeon,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))


        cajasTexto(
            value = nombre,
            onValueChange = { nombre = it },
            label = "Nombre de tu Evento",
            leadingIcon = Icons.Default.Festival
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de Cargar Imagen (Placeholder Neón)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(horizontal = 16.dp)
                .border(2.dp, RosadoNeon.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                .background(Color.Transparent)
                .clickable { /* imagen*/ },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = RosadoNeon, modifier = Modifier.size(40.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text("Cargar Imagen", color = RosadoNeon, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))


        cajasTexto(
            value = fecha,
            onValueChange = { if (it.all { char -> char.isDigit() || char == '/' }) fecha = it },
            label = "Fecha del Evento (DD/MM/AAAA)",
            leadingIcon = Icons.Default.DateRange,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(16.dp))


        TimePickerCaja(
            value = hora,
            onValueChange = { hora = it },
            label = "Hora del Evento (HH:MM)"
        )

        Spacer(modifier = Modifier.height(16.dp))


        cajasTexto(
            value = ubicacion,
            onValueChange = { ubicacion = it },
            label = "Ubicación",
            leadingIcon = Icons.Default.LocationOn,
            trailingIcon = { Icon(Icons.Default.Map, contentDescription = null, tint = IconColor) }
        )

        Spacer(modifier = Modifier.height(16.dp))


        cajasTexto(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = "Descripción",
            leadingIcon = Icons.Default.Description,
            modifier = Modifier.height(120.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )


        if (errorMensaje.isNotEmpty()) {
            Text(
                errorMensaje,
                color = Color.Red,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp, vertical = 8.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(40.dp))


        glowButton(
            text = "REGISTRAR EVENTO",
            onClick = {

                if (nombre.isNotBlank() && fecha.isNotBlank() && hora.isNotBlank() && ubicacion.isNotBlank()) {

                    errorMensaje = ""
                    onNavigateToHome()
                } else {
                    errorMensaje = "Todos los campos son obligatorios"
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}