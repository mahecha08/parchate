package com.universidad.parchate.ui.screens.Event

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.universidad.parchate.R
import com.universidad.parchate.ui.theme.BackgroundPrincipal
import com.universidad.parchate.ui.theme.RosadoNeon
import com.universidad.parchate.ui.theme.TextoSecundario
import com.universidad.parchate.ui.viewmodel.EventDetailViewModel

@Composable
fun EventDetailScreen(
    eventId: String,
    onNavigateBack: () -> Unit = {},
    vm: EventDetailViewModel = viewModel()
) {
    val uiState by vm.uiState.collectAsState()

    LaunchedEffect(eventId) { vm.loadEvent(eventId) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrincipal)
    ) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(
                    color = RosadoNeon,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            uiState.error != null || uiState.event == null -> {
                Text(
                    text = uiState.error ?: "Evento no encontrado",
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center).padding(24.dp),
                    textAlign = TextAlign.Center
                )
            }

            else -> {
                val evento = uiState.event!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // ── Hero image ────────────────────────────────────────
                    Box(modifier = Modifier.fillMaxWidth().height(280.dp)) {
                        if (evento.imagenUrl.isNotBlank()) {
                            AsyncImage(
                                model = evento.imagenUrl,
                                contentDescription = evento.nombreVisible,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(0xFF2A2746))
                            )
                        }

                        // Gradiente inferior para legibilidad
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            BackgroundPrincipal.copy(alpha = 0.85f),
                                            BackgroundPrincipal
                                        ),
                                        startY = 100f
                                    )
                                )
                        )

                        // Botón atrás
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier
                                .padding(top = 44.dp, start = 8.dp)
                                .size(42.dp)
                                .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.atras),
                                tint = Color.White
                            )
                        }

                        // Botón favorito
                        IconButton(
                            onClick = { vm.toggleFavorite(eventId) },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 44.dp, end = 8.dp)
                                .size(42.dp)
                                .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                        ) {
                            Icon(
                                imageVector = if (uiState.isFavorite) Icons.Default.Favorite
                                              else Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                tint = if (uiState.isFavorite) RosadoNeon else Color.White
                            )
                        }

                        // Nombre del evento sobre el gradiente
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(horizontal = 20.dp, vertical = 16.dp)
                        ) {
                            if (evento.categoria.isNotBlank()) {
                                Surface(
                                    color = RosadoNeon.copy(alpha = 0.9f),
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    Text(
                                        text = evento.categoria,
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                            }
                            Text(
                                text = evento.nombreVisible,
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 30.sp
                            )
                        }
                    }

                    // ── Contenido ─────────────────────────────────────────
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    ) {
                        Spacer(modifier = Modifier.height(4.dp))

                        // Chips de info rápida
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val precioText = if (evento.gratis) stringResource(R.string.event_detail_gratis)
                                             else "$ ${"%.0f".format(evento.precio ?: 0.0)}"
                            InfoChip(
                                icon = Icons.Default.AttachMoney,
                                label = precioText,
                                highlighted = evento.gratis
                            )
                            if (evento.modalidad.isNotBlank()) {
                                InfoChip(
                                    icon = if (evento.modalidad.equals("online", ignoreCase = true))
                                               Icons.Default.Wifi else Icons.Default.LocationOn,
                                    label = evento.modalidad.replaceFirstChar { it.titlecase() }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // ── Fecha y hora ──────────────────────────────────
                        SectionTitle(stringResource(R.string.event_detail_cuando))
                        Spacer(modifier = Modifier.height(10.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF25233D))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                DetailRow(
                                    icon = Icons.Default.CalendarMonth,
                                    label = stringResource(R.string.event_detail_fecha),
                                    value = evento.fecha.ifBlank { "—" }
                                )
                                if (evento.hora.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    HorizontalDivider(color = Color.White.copy(alpha = 0.07f))
                                    Spacer(modifier = Modifier.height(10.dp))
                                    DetailRow(
                                        icon = Icons.Default.Schedule,
                                        label = stringResource(R.string.event_detail_hora),
                                        value = evento.hora
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // ── Ubicación ─────────────────────────────────────
                        SectionTitle(stringResource(R.string.event_detail_donde))
                        Spacer(modifier = Modifier.height(10.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF25233D))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                DetailRow(
                                    icon = Icons.Default.LocationOn,
                                    label = stringResource(R.string.event_detail_ubicacion),
                                    value = buildString {
                                        if (evento.ubicacion.isNotBlank()) append(evento.ubicacion)
                                        if (evento.direccion.isNotBlank()) {
                                            if (isNotEmpty()) append("\n")
                                            append(evento.direccion)
                                        }
                                    }.ifBlank { "—" }
                                )
                                if (evento.ciudad.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    HorizontalDivider(color = Color.White.copy(alpha = 0.07f))
                                    Spacer(modifier = Modifier.height(10.dp))
                                    DetailRow(
                                        icon = Icons.Default.Category,
                                        label = stringResource(R.string.register_ciudad),
                                        value = buildString {
                                            append(evento.ciudad)
                                            if (evento.pais.isNotBlank()) append(", ${evento.pais}")
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // ── Descripción ───────────────────────────────────
                        if (evento.descripcion.isNotBlank()) {
                            SectionTitle(stringResource(R.string.event_detail_descripcion))
                            Spacer(modifier = Modifier.height(10.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF25233D))
                            ) {
                                Text(
                                    text = evento.descripcion,
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 14.sp,
                                    lineHeight = 22.sp,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // ── Organizador ───────────────────────────────────
                        SectionTitle(stringResource(R.string.event_detail_organizador))
                        Spacer(modifier = Modifier.height(10.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF25233D))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                DetailRow(
                                    icon = Icons.Default.Person,
                                    label = stringResource(R.string.event_detail_nombre_org),
                                    value = evento.organizadorNombre.ifBlank { "—" }
                                )
                                if (evento.contactoOrganizador.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    HorizontalDivider(color = Color.White.copy(alpha = 0.07f))
                                    Spacer(modifier = Modifier.height(10.dp))
                                    DetailRow(
                                        icon = Icons.Default.Phone,
                                        label = stringResource(R.string.event_detail_contacto),
                                        value = evento.contactoOrganizador
                                    )
                                }
                                if (evento.capacidad != null && evento.capacidad > 0) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    HorizontalDivider(color = Color.White.copy(alpha = 0.07f))
                                    Spacer(modifier = Modifier.height(10.dp))
                                    DetailRow(
                                        icon = Icons.Default.Group,
                                        label = stringResource(R.string.event_detail_capacidad),
                                        value = stringResource(R.string.event_detail_cupos, evento.capacidad)
                                    )
                                }
                            }
                        }

                        // ── Etiquetas ─────────────────────────────────────
                        if (evento.etiquetas.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            SectionTitle(stringResource(R.string.event_detail_etiquetas))
                            Spacer(modifier = Modifier.height(10.dp))
                            TagsRow(evento.etiquetas)
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        color = Color.White,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun DetailRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = RosadoNeon,
            modifier = Modifier.size(20.dp).padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                color = TextoSecundario,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                color = Color.White,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun InfoChip(icon: ImageVector, label: String, highlighted: Boolean = false) {
    Surface(
        color = if (highlighted) RosadoNeon.copy(alpha = 0.15f) else Color(0xFF25233D),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (highlighted) RosadoNeon else TextoSecundario,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = label,
                color = if (highlighted) RosadoNeon else Color.White,
                fontSize = 13.sp,
                fontWeight = if (highlighted) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TagsRow(tags: List<String>) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tags.forEach { tag ->
            AssistChip(
                onClick = {},
                label = { Text(tag, fontSize = 12.sp) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = Color(0xFF3D3A60),
                    labelColor = Color.White
                ),
                border = AssistChipDefaults.assistChipBorder(
                    enabled = true,
                    borderColor = RosadoNeon.copy(alpha = 0.3f)
                )
            )
        }
    }
}
