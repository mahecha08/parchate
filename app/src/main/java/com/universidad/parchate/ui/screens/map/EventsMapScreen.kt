package com.universidad.parchate.ui.screens.map

import android.Manifest
import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.universidad.parchate.data.repository.LocationRepository
import com.universidad.parchate.ui.theme.BackgroundPrincipal
import com.universidad.parchate.ui.theme.RosadoNeon
import com.universidad.parchate.ui.viewmodel.EventsMapViewModel
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
        }
    }

    Scaffold(
        containerColor = BackgroundPrincipal,
        topBar = {
            TopAppBar(
                title = { Text("Eventos en el mapa") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF25233D),
                    titleContentColor = Color.White,
                    navigationIconContentColor = RosadoNeon
                ),
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
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
                Icon(Icons.Default.MyLocation, contentDescription = "Solicitar ubicación")
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
                    mapStyleOptions = ParchateMapStyleOptions
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

                    Marker(
                        state = MarkerState(position = LatLng(lat, lng)),
                        title = event.nombreVisible,
                        snippet = buildString {
                            append(event.ubicacion.ifBlank { event.ciudad })
                            if (event.fecha.isNotBlank()) {
                                append(" • ")
                                append(event.fecha)
                            }
                        }
                    )
                }
            }

            if (!uiState.isLoading && uiState.errorMessage == null) {
                Card(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xE625233D))
                ) {
                    Text(
                        text = "${uiState.events.size} eventos visibles",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        color = Color.White,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
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
