package com.universidad.parchate.ui.screens.map

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.universidad.parchate.data.model.Evento
import com.universidad.parchate.data.repository.LocationRepository
import com.universidad.parchate.ui.theme.BackgroundPrincipal
import com.universidad.parchate.ui.theme.RosadoNeonBack
import com.universidad.parchate.ui.theme.RosadoNeon
import com.universidad.parchate.ui.theme.TextoSecundario
import com.universidad.parchate.ui.viewmodel.EventsMapViewModel
import com.universidad.parchate.R
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

private val DefaultWorldCenter = LatLng(0.0, 0.0)

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsMapScreen(
    onNavigateBack: () -> Unit,
    viewModel: EventsMapViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val locationRepository = remember { LocationRepository(context) }
    val coroutineScope = rememberCoroutineScope()
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(DefaultWorldCenter, 1.5f)
    }
    var pendingCameraTarget by remember { mutableStateOf<LatLng?>(null) }
    var isMapLoaded by remember { mutableStateOf(false) }
    var selectedEventId by rememberSaveable { mutableStateOf<String?>(null) }
    var eventMarkerIcon by remember { mutableStateOf<BitmapDescriptor?>(null) }
    var selectedEventMarkerIcon by remember { mutableStateOf<BitmapDescriptor?>(null) }

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PermissionChecker.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission =
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    suspend fun centerOnCurrentLocation() {
        val currentLocation = locationRepository.getCurrentLocation() ?: return
        pendingCameraTarget = LatLng(currentLocation.latitude, currentLocation.longitude)
    }

    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            centerOnCurrentLocation()
        }
    }

    LaunchedEffect(isMapLoaded, pendingCameraTarget) {
        val target = pendingCameraTarget ?: return@LaunchedEffect
        if (isMapLoaded) {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(target, 14f)
            pendingCameraTarget = null
        }
    }

    LaunchedEffect(isMapLoaded) {
        if (!isMapLoaded) return@LaunchedEffect
        if (eventMarkerIcon == null) {
            eventMarkerIcon = createEventMarkerDescriptor(
                outerColor = RosadoNeon.toArgb(),
                innerColor = Color.White.toArgb(),
                centerDotColor = BackgroundPrincipal.toArgb(),
                isSelected = false
            )
        }
        if (selectedEventMarkerIcon == null) {
            selectedEventMarkerIcon = createEventMarkerDescriptor(
                outerColor = Color.White.toArgb(),
                innerColor = RosadoNeon.toArgb(),
                centerDotColor = Color.White.toArgb(),
                isSelected = true
            )
        }
    }

    LaunchedEffect(uiState.events) {
        if (uiState.events.isEmpty()) {
            selectedEventId = null
            return@LaunchedEffect
        }

        if (selectedEventId == null || uiState.events.none { it.id == selectedEventId }) {
            selectedEventId = uiState.events.first().id
        }
    }

    val selectedEvent = uiState.events.firstOrNull { it.id == selectedEventId }

    Scaffold(
        containerColor = BackgroundPrincipal,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.map_eventos_en_mapa)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF25233D),
                    titleContentColor = Color.White,
                    navigationIconContentColor = RosadoNeon
                ),
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.map_volver))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (!hasLocationPermission) {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    } else {
                        coroutineScope.launch {
                            centerOnCurrentLocation()
                        }
                    }
                },
                containerColor = RosadoNeon,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = stringResource(R.string.map_solicitar_ubicacion))
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    isMyLocationEnabled = hasLocationPermission,
                    mapStyleOptions = ParchateEventsMapStyleOptions
                ),
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                    myLocationButtonEnabled = false
                ),
                onMapLoaded = {
                    isMapLoaded = true
                }
            ) {
                uiState.events.forEach { event ->
                    val lat = event.latitud ?: return@forEach
                    val lng = event.longitud ?: return@forEach
                    val isSelected = event.id == selectedEventId

                    Marker(
                        state = MarkerState(position = LatLng(lat, lng)),
                        title = event.nombreVisible,
                        icon = if (isSelected) selectedEventMarkerIcon else eventMarkerIcon,
                        onClick = {
                            selectedEventId = event.id
                            pendingCameraTarget = LatLng(lat, lng)
                            true
                        }
                    )
                }
            }

            if (!isMapsConfigured) {
                MapConfigurationWarning()
            }

            if (!uiState.isLoading && uiState.errorMessage == null) {
                Card(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xE625233D))
                ) {
                    Text(
                        text = stringResource(R.string.map_eventos_visibles, uiState.events.size),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        color = Color.White,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            selectedEvent?.let { event ->
                EventSpotlightCard(
                    event = event,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 16.dp, vertical = 24.dp)
                )
            }

            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = RosadoNeon
                    )
                }

                uiState.errorMessage != null -> {
                    Text(
                        text = uiState.errorMessage.orEmpty(),
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun EventSpotlightCard(
    event: Evento,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        border = BorderStroke(1.5.dp, RosadoNeon.copy(alpha = 0.45f)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F3F7))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (event.imagenUrl.isNotBlank()) {
                AsyncImage(
                    model = event.imagenUrl,
                    contentDescription = event.nombreVisible,
                    modifier = Modifier
                        .size(84.dp)
                        .background(Color(0xFFFFE3EA), MaterialTheme.shapes.large),
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(84.dp)
                        .background(RosadoNeonBack, MaterialTheme.shapes.large),
                    contentAlignment = Alignment.Center
                ) {
                    val inicialCat = stringResource(R.string.map_inicial_categoria)
                    Text(
                        text = event.categoria.take(1).ifBlank { inicialCat },
                        color = BackgroundPrincipal,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val categoriaEvento = stringResource(R.string.map_evento)
                    Surface(
                        color = RosadoNeon,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = event.categoria.ifBlank { categoriaEvento },
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    if (event.gratis || (event.precio ?: 0.0) == 0.0) {
                        val gratisText = stringResource(R.string.map_gratis)
                        Surface(
                            modifier = Modifier.padding(start = 8.dp),
                            color = Color(0xFFFFE0E8),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = gratisText,
                                color = RosadoNeon,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Text(
                    text = event.nombreVisible,
                    color = BackgroundPrincipal,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 10.dp)
                )

                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null,
                        tint = RosadoNeon,
                        modifier = Modifier.size(16.dp)
                    )
                    val ubicacionPorConfirmar = stringResource(R.string.map_ubicacion_por_confirmar)
                    Text(
                        text = event.ubicacion.ifBlank { event.ciudad.ifBlank { ubicacionPorConfirmar } },
                        color = TextoSecundario,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(start = 6.dp)
                    )
                }

                Row(
                    modifier = Modifier.padding(top = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = RosadoNeon,
                        modifier = Modifier.size(16.dp)
                    )
                    val fechaPorDefinir = stringResource(R.string.map_fecha_por_definir)
                    val separador = stringResource(R.string.map_separador_hora)
                    Text(
                        text = buildString {
                            append(event.fecha.ifBlank { fechaPorDefinir })
                            if (event.hora.isNotBlank()) {
                                append(separador)
                                append(event.hora)
                            }
                        },
                        color = TextoSecundario,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(start = 6.dp)
                    )
                }

                if (!event.gratis && (event.precio ?: 0.0) > 0.0) {
                    Row(
                        modifier = Modifier.padding(top = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalOffer,
                            contentDescription = null,
                            tint = RosadoNeon,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = stringResource(R.string.map_precio_formato, event.precio?.toInt() ?: 0),
                            color = BackgroundPrincipal,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(start = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun createEventMarkerDescriptor(
    outerColor: Int,
    innerColor: Int,
    centerDotColor: Int,
    isSelected: Boolean
): BitmapDescriptor {
    val width = if (isSelected) 110 else 92
    val height = if (isSelected) 138 else 116
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val bodyRadius = if (isSelected) 28f else 22f
    val centerX = width / 2f
    val centerY = if (isSelected) 42f else 36f
    val tipY = if (isSelected) 118f else 98f
    val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.Black.copy(alpha = if (isSelected) 0.30f else 0.22f).toArgb()
    }
    val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = outerColor
        style = Paint.Style.FILL
    }
    val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.White.toArgb()
        style = Paint.Style.STROKE
        strokeWidth = if (isSelected) 6f else 4f
    }
    val innerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = innerColor
        style = Paint.Style.FILL
    }
    val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = centerDotColor
        style = Paint.Style.FILL
    }

    val pinPath = Path().apply {
        moveTo(centerX, tipY)
        lineTo(centerX - bodyRadius * 0.75f, centerY + bodyRadius * 0.7f)
        lineTo(centerX + bodyRadius * 0.75f, centerY + bodyRadius * 0.7f)
        close()
    }

    canvas.drawCircle(centerX, tipY + 8f, bodyRadius * 0.55f, shadowPaint)
    canvas.drawPath(pinPath, fillPaint)
    canvas.drawCircle(centerX, centerY, bodyRadius, fillPaint)
    canvas.drawPath(pinPath, strokePaint)
    canvas.drawCircle(centerX, centerY, bodyRadius, strokePaint)
    canvas.drawCircle(centerX, centerY, bodyRadius * 0.55f, innerPaint)
    canvas.drawCircle(centerX, centerY, if (isSelected) 8f else 6f, dotPaint)

    return BitmapDescriptorFactory.fromBitmap(bitmap)
}
