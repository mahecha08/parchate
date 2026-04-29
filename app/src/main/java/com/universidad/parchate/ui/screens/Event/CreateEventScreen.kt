package com.universidad.parchate.ui.screens.create

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Festival
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.universidad.parchate.R
import com.universidad.parchate.ui.components.TimePickerCaja
import com.universidad.parchate.ui.components.cajasTexto
import com.universidad.parchate.ui.components.glowButton
import com.universidad.parchate.ui.theme.BackgroundPrincipal
import com.universidad.parchate.ui.theme.IconColor
import com.universidad.parchate.ui.theme.RosadoNeon
import com.universidad.parchate.ui.viewmodel.CreateEventViewModel

private val categorias = listOf(
    R.string.categoria_concierto,
    R.string.categoria_festival,
    R.string.categoria_teatro,
    R.string.categoria_feria,
    R.string.categoria_cultural,
    R.string.categoria_deportes,
    R.string.categoria_tecnologia
)

private val modalidades = listOf(
    R.string.modalidad_presencial,
    R.string.modalidad_online
)

@Composable
fun CreateEventScreen(
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToMapPicker: () -> Unit,
    selectedLatitud: Double? = null,
    selectedLongitud: Double? = null,
    selectedPais: String? = null,
    selectedCiudad: String? = null,
    selectedDireccion: String? = null,
    selectedUbicacion: String? = null,
    createEventViewModel: CreateEventViewModel = viewModel()
) {
    val uiState by createEventViewModel.uiState.collectAsState()
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        createEventViewModel.onImageSelected(uri)
    }

    LaunchedEffect(
        selectedLatitud,
        selectedLongitud,
        selectedPais,
        selectedCiudad,
        selectedDireccion,
        selectedUbicacion
    ) {
        createEventViewModel.applySelectedLocation(
            latitud = selectedLatitud,
            longitud = selectedLongitud,
            pais = selectedPais,
            ciudad = selectedCiudad,
            direccion = selectedDireccion,
            ubicacion = selectedUbicacion
        )
    }

    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            createEventViewModel.consumeSuccess()
            onNavigateToHome()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrincipal)
            .padding(vertical = 10.dp)
            .verticalScroll(rememberScrollState())
    ) {
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .padding(top = 40.dp, start = 8.dp)
                .padding(vertical = 20.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.atras),
                tint = RosadoNeon
            )
        }

        Text(
            text = stringResource(R.string.create_titulo),
            color = RosadoNeon,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        cajasTexto(
            value = uiState.titulo,
            onValueChange = {
                createEventViewModel.onFieldChange { current -> current.copy(titulo = it) }
            },
            label = stringResource(R.string.create_titulo_evento),
            leadingIcon = Icons.Default.Festival
        )

        Spacer(modifier = Modifier.height(16.dp))

        ImageSelector(
            imageUri = uiState.imageUri,
            onClick = { imagePicker.launch("image/*") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        SimpleSelector(
            title = stringResource(R.string.create_categoria),
            options = categorias.map { stringResource(it) },
            selected = uiState.categoria,
            onSelected = { value ->
                createEventViewModel.onFieldChange { current -> current.copy(categoria = value) }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        cajasTexto(
            value = uiState.fecha,
            onValueChange = { input ->
                if (input.all { char -> char.isDigit() || char == '-' }) {
                    createEventViewModel.onFieldChange { current -> current.copy(fecha = input) }
                }
            },
            label = stringResource(R.string.create_fecha_evento),
            leadingIcon = Icons.Default.DateRange,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(16.dp))

        TimePickerCaja(
            value = uiState.hora,
            onValueChange = {
                createEventViewModel.onFieldChange { current -> current.copy(hora = it) }
            },
            label = stringResource(R.string.create_hora_evento)
        )

        Spacer(modifier = Modifier.height(16.dp))

        SimpleSelector(
            title = stringResource(R.string.create_modalidad),
            options = modalidades.map { stringResource(it) },
            selected = uiState.modalidad,
            onSelected = { value ->
                createEventViewModel.onFieldChange { current -> current.copy(modalidad = value) }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        glowButton(
            text = "SELECCIONAR UBICACION EN EL MAPA",
            onClick = onNavigateToMapPicker
        )

        Spacer(modifier = Modifier.height(16.dp))

        cajasTexto(
            value = uiState.pais,
            onValueChange = {
                createEventViewModel.onFieldChange { current -> current.copy(pais = it) }
            },
            label = "Pais",
            leadingIcon = Icons.Default.Public,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )

        Spacer(modifier = Modifier.height(16.dp))

        cajasTexto(
            value = uiState.ciudad,
            onValueChange = {
                createEventViewModel.onFieldChange { current -> current.copy(ciudad = it) }
            },
            label = stringResource(R.string.create_ciudad),
            leadingIcon = Icons.Default.LocationOn,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )

        Spacer(modifier = Modifier.height(16.dp))

        cajasTexto(
            value = uiState.direccion,
            onValueChange = {
                createEventViewModel.onFieldChange { current -> current.copy(direccion = it) }
            },
            label = "Direccion detectada",
            leadingIcon = Icons.Default.Map,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )

        Spacer(modifier = Modifier.height(16.dp))

        cajasTexto(
            value = uiState.ubicacion,
            onValueChange = {
                createEventViewModel.onFieldChange { current -> current.copy(ubicacion = it) }
            },
            label = stringResource(R.string.create_lugar_evento),
            leadingIcon = Icons.Default.Place,
            trailingIcon = { Icon(Icons.Default.Map, contentDescription = null, tint = IconColor) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.modalidad.equals("online", ignoreCase = true)) {
            Text(
                text = "Para eventos online, el mapa es opcional. Puedes usar la ubicacion para poner el enlace o la plataforma.",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                color = Color.White.copy(alpha = 0.75f),
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        cajasTexto(
            value = uiState.descripcion,
            onValueChange = {
                createEventViewModel.onFieldChange { current -> current.copy(descripcion = it) }
            },
            label = stringResource(R.string.create_descripcion),
            leadingIcon = Icons.Default.Description,
            modifier = Modifier.height(120.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )

        Spacer(modifier = Modifier.height(16.dp))

        cajasTexto(
            value = uiState.organizadorNombre,
            onValueChange = {
                createEventViewModel.onFieldChange { current -> current.copy(organizadorNombre = it) }
            },
            label = stringResource(R.string.create_nombre_organizador),
            leadingIcon = Icons.Default.Person,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )

        Spacer(modifier = Modifier.height(16.dp))

        cajasTexto(
            value = uiState.contactoOrganizador,
            onValueChange = {
                createEventViewModel.onFieldChange { current -> current.copy(contactoOrganizador = it) }
            },
            label = stringResource(R.string.create_contacto_organizador),
            leadingIcon = Icons.Default.Phone,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )

        Spacer(modifier = Modifier.height(16.dp))

        cajasTexto(
            value = uiState.capacidad,
            onValueChange = {
                createEventViewModel.onFieldChange { current ->
                    current.copy(capacidad = it.filter(Char::isDigit))
                }
            },
            label = stringResource(R.string.create_capacidad_maxima),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(16.dp))

        cajasTexto(
            value = uiState.etiquetas,
            onValueChange = {
                createEventViewModel.onFieldChange { current -> current.copy(etiquetas = it) }
            },
            label = stringResource(R.string.create_etiquetas),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = uiState.gratis,
                    onCheckedChange = { checked ->
                        createEventViewModel.onFieldChange { current ->
                            current.copy(
                                gratis = checked,
                                precio = if (checked) "" else current.precio
                            )
                        }
                    }
                )
                Text(stringResource(R.string.create_evento_gratuito), color = Color.White)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = uiState.destacado,
                    onCheckedChange = { checked ->
                        createEventViewModel.onFieldChange { current -> current.copy(destacado = checked) }
                    }
                )
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = RosadoNeon
                )
                Text(stringResource(R.string.create_destacado), color = Color.White)
            }
        }

        if (!uiState.gratis) {
            Spacer(modifier = Modifier.height(16.dp))
            cajasTexto(
                value = uiState.precio,
                onValueChange = {
                    createEventViewModel.onFieldChange { current -> current.copy(precio = it) }
                },
                label = stringResource(R.string.create_precio),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (uiState.latitud.isNotBlank() && uiState.longitud.isNotBlank()) {
            Text(
                text = "Coordenadas: ${uiState.latitud}, ${uiState.longitud}",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                color = Color.White.copy(alpha = 0.75f),
                style = MaterialTheme.typography.bodySmall
            )
        }

        uiState.errorMessage?.let { error ->
            Text(
                text = error,
                color = Color.Red,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp, vertical = 8.dp),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            if (uiState.isSaving) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CircularProgressIndicator(color = RosadoNeon)
                    Text(
                        text = "Guardando evento...",
                        color = Color.White
                    )
                }
            } else {
                glowButton(
                    text = stringResource(R.string.create_registrar_evento),
                    onClick = createEventViewModel::saveEvent
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun ImageSelector(
    imageUri: Uri?,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(horizontal = 16.dp)
            .border(2.dp, RosadoNeon.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            .background(Color.Transparent)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (imageUri != null) {
            AsyncImage(
                model = imageUri,
                contentDescription = stringResource(R.string.create_imagen_evento),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.AddPhotoAlternate,
                    contentDescription = null,
                    tint = RosadoNeon,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.create_cargar_imagen),
                    color = RosadoNeon,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun SimpleSelector(
    title: String,
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = title,
            color = Color.White,
            style = MaterialTheme.typography.titleSmall
        )
        options.forEach { option ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = selected == option,
                    onClick = { onSelected(option) }
                )
                Text(
                    text = option.replaceFirstChar { char ->
                        if (char.isLowerCase()) char.titlecase() else char.toString()
                    },
                    color = Color.White
                )
            }
        }
    }
}
