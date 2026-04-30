package com.universidad.parchate.ui.screens.map

import android.Manifest
import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.universidad.parchate.data.repository.LocationAutocompleteSuggestion
import com.universidad.parchate.data.repository.LocationRepository
import com.universidad.parchate.ui.theme.BackgroundPrincipal
import com.universidad.parchate.ui.theme.RosadoNeon
import com.universidad.parchate.ui.viewmodel.MapPickerViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val DefaultMapCenter = LatLng(4.7110, -74.0721)

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapPickerScreen(
    onNavigateBack: () -> Unit,
    onLocationSelected: (
        latitud: Double,
        longitud: Double,
        pais: String,
        ciudad: String,
        direccion: String,
        ubicacion: String
    ) -> Unit,
    viewModel: MapPickerViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val locationRepository = remember { LocationRepository(context) }
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PermissionChecker.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PermissionChecker.PERMISSION_GRANTED
        )
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(DefaultMapCenter, 12f)
    }
    var pendingCameraTarget by remember { mutableStateOf<LatLng?>(null) }
    var isMapLoaded by remember { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<LocationAutocompleteSuggestion>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }
    var searchErrorMessage by remember { mutableStateOf<String?>(null) }
    var autocompleteSessionToken by remember { mutableStateOf<AutocompleteSessionToken?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission =
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    suspend fun centerOnCurrentLocation() {
        val currentLocation = locationRepository.getCurrentLocation() ?: return
        val currentLatLng = LatLng(currentLocation.latitude, currentLocation.longitude)
        pendingCameraTarget = currentLatLng
        viewModel.updateSelectedLocation(currentLatLng)
    }

    suspend fun loadSearchSuggestions() {
        val query = searchQuery.trim()
        if (query.isBlank()) {
            searchResults = emptyList()
            searchErrorMessage = null
            autocompleteSessionToken = null
            isSearching = false
            return
        }

        if (query.length < 2) {
            searchResults = emptyList()
            searchErrorMessage = null
            isSearching = false
            return
        }

        if (autocompleteSessionToken == null) {
            autocompleteSessionToken = AutocompleteSessionToken.newInstance()
        }

        isSearching = true
        searchErrorMessage = null

        val results = locationRepository.autocompleteLocations(
            query = query,
            sessionToken = autocompleteSessionToken
        )
        searchResults = results
        searchErrorMessage = if (results.isEmpty()) {
            "No encontramos lugares con ese nombre"
        } else {
            null
        }
        isSearching = false
    }

    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            centerOnCurrentLocation()
        }
    }

    LaunchedEffect(isMapLoaded, pendingCameraTarget) {
        val target = pendingCameraTarget ?: return@LaunchedEffect
        if (isMapLoaded) {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(target, 15f)
        }
    }

    LaunchedEffect(searchQuery) {
        val query = searchQuery.trim()
        if (query.isBlank()) {
            searchResults = emptyList()
            searchErrorMessage = null
            autocompleteSessionToken = null
            isSearching = false
            return@LaunchedEffect
        }

        delay(350)
        loadSearchSuggestions()
    }

    Scaffold(
        containerColor = BackgroundPrincipal,
        topBar = {
            TopAppBar(
                title = { Text("Seleccionar ubicación") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF25233D),
                    titleContentColor = Color.White,
                    navigationIconContentColor = RosadoNeon
                ),
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FloatingActionButton(
                    onClick = {
                        viewModel.selectedLatLng?.let { selected ->
                            onLocationSelected(
                                selected.latitude,
                                selected.longitude,
                                uiState.pais,
                                uiState.ciudad,
                                uiState.direccion,
                                uiState.ubicacion
                            )
                        }
                    },
                    containerColor = RosadoNeon,
                    contentColor = Color.White
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Confirmar ubicación"
                    )
                }

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
                    containerColor = Color(0xFF2D2947),
                    contentColor = Color.White
                ) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "Mi ubicación"
                    )
                }
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
                },
                onMapClick = { latLng ->
                    searchResults = emptyList()
                    searchErrorMessage = null
                    autocompleteSessionToken = null
                    viewModel.updateSelectedLocation(latLng)
                }
            ) {
                viewModel.selectedLatLng?.let { selected ->
                    Marker(
                        state = MarkerState(position = selected),
                        title = "Ubicación seleccionada"
                    )
                }
            }

            if (!isMapsConfigured) {
                MapConfigurationWarning()
            }

            Card(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xE625233D)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { value ->
                            searchQuery = value
                            if (value.isBlank()) {
                                searchResults = emptyList()
                                searchErrorMessage = null
                                autocompleteSessionToken = null
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text("Buscar lugar, barrio o dirección")
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = RosadoNeon
                            )
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        keyboardController?.hide()
                                        loadSearchSuggestions()
                                    }
                                }
                            ) {
                                if (isSearching) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.height(18.dp),
                                        color = RosadoNeon,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Buscar",
                                        tint = RosadoNeon
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                coroutineScope.launch {
                                    keyboardController?.hide()
                                    loadSearchSuggestions()
                                }
                            }
                        ),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF312E54),
                            unfocusedContainerColor = Color(0xFF312E54),
                            focusedIndicatorColor = RosadoNeon,
                            unfocusedIndicatorColor = RosadoNeon.copy(alpha = 0.45f),
                            cursorColor = RosadoNeon,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedPlaceholderColor = Color.White.copy(alpha = 0.55f),
                            unfocusedPlaceholderColor = Color.White.copy(alpha = 0.45f)
                        )
                    )

                    searchErrorMessage?.let { message ->
                        Text(
                            text = message,
                            color = Color(0xFFFFB4C2),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    if (searchResults.isNotEmpty()) {
                        Column(
                            modifier = Modifier.heightIn(max = 220.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            searchResults.forEach { suggestion ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 2.dp)
                                ) {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        onClick = {
                                            coroutineScope.launch {
                                                keyboardController?.hide()
                                                isSearching = true
                                                searchErrorMessage = null

                                                val result = locationRepository
                                                    .resolveAutocompleteSuggestion(suggestion)

                                                if (result != null) {
                                                    searchQuery = suggestion.primaryText
                                                    searchResults = emptyList()
                                                    searchErrorMessage = null
                                                    pendingCameraTarget = result.latLng
                                                    autocompleteSessionToken = null
                                                    viewModel.setSelectedLocation(
                                                        latLng = result.latLng,
                                                        addressResult = result.asAddressResult()
                                                    )
                                                } else {
                                                    searchErrorMessage =
                                                        "No pudimos obtener la ubicación exacta"
                                                }

                                                isSearching = false
                                            }
                                        },
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color(0xFF312E54)
                                        ),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(12.dp),
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = suggestion.primaryText.ifBlank {
                                                    "Lugar encontrado"
                                                },
                                                color = Color.White,
                                                style = MaterialTheme.typography.titleSmall
                                            )
                                            if (suggestion.secondaryText.isNotBlank()) {
                                                Text(
                                                    text = suggestion.secondaryText,
                                                    color = Color.White.copy(alpha = 0.72f),
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF25233D)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Ubicación seleccionada",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )

                    if (uiState.isResolvingAddress) {
                        CircularProgressIndicator(
                            modifier = Modifier.height(24.dp),
                            color = RosadoNeon
                        )
                    } else {
                        Text(
                            text = "País: ${uiState.pais.ifBlank { "No disponible" }}",
                            color = Color.White
                        )
                        Text(
                            text = "Ciudad: ${uiState.ciudad.ifBlank { "No disponible" }}",
                            color = Color.White
                        )
                        Text(
                            text = "Dirección: ${uiState.direccion.ifBlank { "No disponible" }}",
                            color = Color.White
                        )
                        Text(
                            text = "Lugar: ${uiState.ubicacion.ifBlank { "No disponible" }}",
                            color = Color.White
                        )
                    }

                    if (uiState.errorMessage != null) {
                        Text(
                            text = uiState.errorMessage.orEmpty(),
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.selectedLatLng?.let { selected ->
                                onLocationSelected(
                                    selected.latitude,
                                    selected.longitude,
                                    uiState.pais,
                                    uiState.ciudad,
                                    uiState.direccion,
                                    uiState.ubicacion
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = RosadoNeon),
                        shape = RoundedCornerShape(14.dp),
                        enabled = viewModel.selectedLatLng != null && !uiState.isResolvingAddress
                    ) {
                        Text(
                            text = "Usar esta ubicación",
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
