package com.universidad.parchate.ui.screens.Home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.universidad.parchate.R
import com.universidad.parchate.ui.components.EventCard
import com.universidad.parchate.ui.components.cajasTexto
import com.universidad.parchate.ui.theme.BackgroundPrincipal
import com.universidad.parchate.ui.theme.RosadoNeon
import com.universidad.parchate.ui.viewmodel.HomeViewModel

private val categorias = listOf(
    R.string.categoria_todos,
    R.string.categoria_concierto,
    R.string.categoria_festival,
    R.string.categoria_teatro,
    R.string.categoria_feria,
    R.string.categoria_cultural,
    R.string.categoria_deportes,
    R.string.categoria_tecnologia
)
private val modalidades = listOf(
    R.string.modalidad_todas,
    R.string.modalidad_presencial,
    R.string.modalidad_online
)

@Composable
fun HomeScreen(
    onNavigateToCreate: () -> Unit = {},
    onNavigateToMap: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToFavorites: () -> Unit = {},
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showFilters by rememberSaveable { mutableStateOf(false) }

    // Resolve string resource IDs outside LazyColumn
    val categoriasResueltas = categorias.map { stringResource(it) }
    val modalidadesResueltas = modalidades.map { stringResource(it) }

    Scaffold(
        bottomBar = {
            BottomNavParchate(
                onAddClick = onNavigateToCreate,
                onMapClick = onNavigateToMap,
                onProfileClick = onNavigateToProfile,
                onFavoritesClick = onNavigateToFavorites
            )
        },
        containerColor = BackgroundPrincipal
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = stringResource(R.string.home_titulo), color = RosadoNeon, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                cajasTexto(
                    value = uiState.filters.search,
                    onValueChange = { value ->
                        viewModel.updateFilters { current -> current.copy(search = value) }
                    },
                    label = stringResource(R.string.home_buscar_eventos),
                    leadingIcon = Icons.Default.Search,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = { showFilters = !showFilters }) {
                    Icon(Icons.Default.Tune, contentDescription = null, tint = Color.White)
                }
            }

            if (showFilters) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = stringResource(R.string.home_filtros), color = Color.White, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.height(160.dp),
                    contentPadding = PaddingValues(bottom = 8.dp)
                ) {
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            categoriasResueltas.take(4).forEach { categoria ->
                                FilterChip(
                                    selected = uiState.filters.categoria == categoria,
                                    onClick = {
                                        viewModel.updateFilters { it.copy(categoria = categoria) }
                                    },
                                    label = { Text(categoria) }
                                )
                            }
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            categoriasResueltas.drop(4).forEach { categoria ->
                                FilterChip(
                                    selected = uiState.filters.categoria == categoria,
                                    onClick = {
                                        viewModel.updateFilters { it.copy(categoria = categoria) }
                                    },
                                    label = { Text(categoria) }
                                )
                            }
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                        cajasTexto(
                            value = uiState.filters.ciudad,
                            onValueChange = { city ->
                                viewModel.updateFilters { it.copy(ciudad = city) }
                            },
                            label = stringResource(R.string.home_filtrar_ciudad)
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            modalidadesResueltas.forEach { modalidad ->
                                AssistChip(
                                    onClick = {
                                        viewModel.updateFilters { it.copy(modalidad = modalidad) }
                                    },
                                    label = { Text(modalidad) }
                                )
                            }
                            FilterChip(
                                selected = uiState.filters.soloGratis,
                                onClick = {
                                    viewModel.updateFilters { it.copy(soloGratis = !it.soloGratis) }
                                },
                                label = { Text(stringResource(R.string.home_solo_gratis)) }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when {
                uiState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = RosadoNeon)
                    }
                }

                uiState.errorMessage != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = uiState.errorMessage.orEmpty(), color = Color.White)
                    }
                }

                uiState.filteredEvents.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = stringResource(R.string.home_no_eventos), color = Color.White)
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(uiState.filteredEvents, key = { it.id }) { evento ->
                            EventCard(
                                evento = evento,
                                onDetailClick = { },
                                isFavorite = evento.id in uiState.favoriteEventIds,
                                onFavoriteClick = { viewModel.toggleFavorite(evento.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavParchate(
    onAddClick: () -> Unit,
    onMapClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onFavoritesClick: () -> Unit = {}
) {
    NavigationBar(
        containerColor = Color(0xFF1B172E),
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = true,
            onClick = onHomeClick,
            icon = { Icon(Icons.Default.Home, contentDescription = null, tint = RosadoNeon) },
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
        )
        NavigationBarItem(
            selected = false,
            onClick = onMapClick,
            icon = { Icon(Icons.Default.Map, contentDescription = null, tint = Color.White) },
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
        )
        NavigationBarItem(
            selected = false,
            onClick = { onAddClick() },
            icon = {
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = stringResource(R.string.home_crear_evento),
                    tint = Color.White
                )
            },
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
        )
        NavigationBarItem(
            selected = false,
            onClick = onFavoritesClick,
            icon = { Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.White) },
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
        )
        NavigationBarItem(
            selected = false,
            onClick = onProfileClick,
            icon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color.White) },
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
        )
    }
}
