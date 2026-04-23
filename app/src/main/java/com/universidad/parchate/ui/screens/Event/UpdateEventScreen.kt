package com.universidad.parchate.ui.screens.Event
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.universidad.parchate.ui.components.cajasTexto
import com.universidad.parchate.ui.components.glowButton
import com.universidad.parchate.ui.theme.IconColor
import com.universidad.parchate.ui.theme.RosadoNeon
import com.universidad.parchate.ui.viewmodel.UpdateEventViewModel

private val categorias = listOf("Concierto", "Festival", "Teatro", "Feria", "Cultural", "Deportes", "Tecnología")
private val modalidades = listOf("Presencial", "Online")

@Composable
fun UpdateEventScreen(
    eventId: String,
    onNavigateBack: () -> Unit,
    viewModel: UpdateEventViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        viewModel.onImageSelected(uri)
    }


    LaunchedEffect(eventId) {
        viewModel.loadEvent(eventId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1B172E))
            .padding(vertical = 10.dp)
            .verticalScroll(rememberScrollState())
    ) {
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.padding(top = 40.dp, start = 8.dp).padding(vertical = 20.dp)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = null, tint = RosadoNeon)
        }

        Text(
            text = "Actualizar Evento",
            color = RosadoNeon,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
        )

        if (uiState.isLoading && uiState.titulo.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = RosadoNeon)
            }
        } else {
            Spacer(modifier = Modifier.height(24.dp))

            cajasTexto(
                value = uiState.titulo,
                onValueChange = { viewModel.onFieldChange { current -> current.copy(titulo = it) } },
                label = stringResource(R.string.create_titulo_evento),
                leadingIcon = Icons.Default.Festival
            )

            Spacer(modifier = Modifier.height(16.dp))

            UpdateImageSelector(
                newUri = uiState.imageUri,
                currentUrl = uiState.currentImageUrl,
                onClick = { imagePicker.launch("image/*") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            SimpleSelector(
                title = stringResource(R.string.create_categoria),
                options = categorias,
                selected = uiState.categoria,
                onSelected = { value -> viewModel.onFieldChange { it.copy(categoria = value) } }
            )

            Spacer(modifier = Modifier.height(16.dp))

            cajasTexto(
                value = uiState.fecha,
                onValueChange = { viewModel.onFieldChange { current -> current.copy(fecha = it) } },
                label = stringResource(R.string.create_fecha_evento),
                leadingIcon = Icons.Default.DateRange,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(16.dp))

            cajasTexto(
                value = uiState.hora,
                onValueChange = { viewModel.onFieldChange { current -> current.copy(hora = it) } },
                label = stringResource(R.string.create_hora_evento),
                leadingIcon = Icons.Default.Schedule,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(16.dp))

            cajasTexto(
                value = uiState.ubicacion,
                onValueChange = { viewModel.onFieldChange { current -> current.copy(ubicacion = it) } },
                label = stringResource(R.string.create_lugar_evento),
                leadingIcon = Icons.Default.LocationOn,
                trailingIcon = { Icon(Icons.Default.Map, contentDescription = null, tint = IconColor) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            cajasTexto(
                value = uiState.direccion,
                onValueChange = { viewModel.onFieldChange { current -> current.copy(direccion = it) } },
                label = stringResource(R.string.create_direccion),
                leadingIcon = Icons.Default.Map
            )

            Spacer(modifier = Modifier.height(16.dp))

            cajasTexto(
                value = uiState.ciudad,
                onValueChange = { viewModel.onFieldChange { current -> current.copy(ciudad = it) } },
                label = stringResource(R.string.create_ciudad)
            )

            Spacer(modifier = Modifier.height(16.dp))

            SimpleSelector(
                title = stringResource(R.string.create_modalidad),
                options = modalidades,
                selected = uiState.modalidad,
                onSelected = { value -> viewModel.onFieldChange { it.copy(modalidad = value) } }
            )

            Spacer(modifier = Modifier.height(16.dp))

            cajasTexto(
                value = uiState.descripcion,
                onValueChange = { viewModel.onFieldChange { current -> current.copy(descripcion = it) } },
                label = stringResource(R.string.create_descripcion),
                leadingIcon = Icons.Default.Description,
                modifier = Modifier.height(120.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            cajasTexto(
                value = uiState.organizadorNombre,
                onValueChange = { viewModel.onFieldChange { current -> current.copy(organizadorNombre = it) } },
                label = stringResource(R.string.create_nombre_organizador),
                leadingIcon = Icons.Default.Person
            )

            Spacer(modifier = Modifier.height(16.dp))

            cajasTexto(
                value = uiState.contactoOrganizador,
                onValueChange = { viewModel.onFieldChange { current -> current.copy(contactoOrganizador = it) } },
                label = stringResource(R.string.create_contacto_organizador),
                leadingIcon = Icons.Default.Phone
            )

            Spacer(modifier = Modifier.height(16.dp))

            cajasTexto(
                value = uiState.capacidad,
                onValueChange = { viewModel.onFieldChange { current -> current.copy(capacidad = it.filter(Char::isDigit)) } },
                label = stringResource(R.string.create_capacidad_maxima),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(16.dp))

            cajasTexto(
                value = uiState.etiquetas,
                onValueChange = { viewModel.onFieldChange { current -> current.copy(etiquetas = it) } },
                label = stringResource(R.string.create_etiquetas)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = uiState.gratis,
                        onCheckedChange = { checked ->
                            viewModel.onFieldChange { current ->
                                current.copy(gratis = checked, precio = if (checked) "" else current.precio)
                            }
                        }
                    )
                    Text(stringResource(R.string.create_evento_gratuito), color = Color.White)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = uiState.destacado,
                        onCheckedChange = { checked ->
                            viewModel.onFieldChange { current -> current.copy(destacado = checked) }
                        }
                    )
                    Icon(Icons.Default.Star, contentDescription = null, tint = RosadoNeon)
                    Text(stringResource(R.string.create_destacado), color = Color.White)
                }
            }

            if (!uiState.gratis) {
                Spacer(modifier = Modifier.height(16.dp))
                cajasTexto(
                    value = uiState.precio,
                    onValueChange = { viewModel.onFieldChange { current -> current.copy(precio = it) } },
                    label = stringResource(R.string.create_precio),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            uiState.errorMessage?.let {
                Text(text = it, color = Color.Red, modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp), textAlign = TextAlign.Center)
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = RosadoNeon)
                }
            } else {
                glowButton(
                    text = "Guardar Cambios",
                    onClick = {
                        viewModel.updateEvent(eventId, onSuccess = onNavigateBack)
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun UpdateImageSelector(
    newUri: Uri?,
    currentUrl: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(horizontal = 16.dp)
            .border(2.dp, RosadoNeon.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        val imageSource = newUri ?: currentUrl
        if (imageSource != null && imageSource.toString().isNotEmpty()) {
            AsyncImage(
                model = imageSource,
                contentDescription = null,
                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Crop
            )
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = RosadoNeon, modifier = Modifier.size(40.dp))
                Text("Cambiar imagen", color = RosadoNeon, fontSize = 14.sp)
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
        Text(text = title, color = Color.White, style = MaterialTheme.typography.titleSmall)
        options.forEach { option ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = selected == option, onClick = { onSelected(option) })
                Text(text = option, color = Color.White)
            }
        }
    }
}