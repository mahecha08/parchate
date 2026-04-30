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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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

@Composable
fun HomeScreen(
    onNavigateToCreate: () -> Unit = {},
    onNavigateToMap: () -> Unit = {},
    onNavigateToChatbot: () -> Unit = {},
    onNavigateToCalendar: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToFavorites: () -> Unit = {},
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showFilters by rememberSaveable { mutableStateOf(false) }
    val allCategoryLabel = stringResource(R.string.categoria_todos)
    val allModalityLabel = stringResource(R.string.modalidad_todas)
    val presencialLabel = stringResource(R.string.modalidad_presencial)
    val onlineLabel = stringResource(R.string.modalidad_online)
    val availableCategories = remember(uiState.events, allCategoryLabel) {
        listOf(allCategoryLabel) + uiState.events
            .map { it.categoria.trim() }
            .filter { it.isNotBlank() }
            .distinctBy { it.lowercase() }
            .sortedBy { it.lowercase() }
    }
    val modalityOptions = remember(allModalityLabel, presencialLabel, onlineLabel) {
        listOf(allModalityLabel, presencialLabel, onlineLabel)
    }
    val hasActiveFilters = remember(uiState.filters, allCategoryLabel, allModalityLabel) {
        uiState.filters.search.isNotBlank() ||
            !uiState.filters.categoria.equals(allCategoryLabel, ignoreCase = true) ||
            uiState.filters.ciudad.isNotBlank() ||
            uiState.filters.soloGratis ||
            !uiState.filters.modalidad.equals(allModalityLabel, ignoreCase = true)
    }

    Scaffold(
        bottomBar = {
            BottomNavParchate(
                onAddClick = onNavigateToCreate,
                onMapClick = onNavigateToMap,
                onProfileClick = onNavigateToProfile,
                onFavoritesClick = onNavigateToFavorites
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToChatbot,
                containerColor = RosadoNeon,
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Default.Forum,
                    contentDescription = stringResource(R.string.home_abrir_chat)
                )
            }
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
                Text(
                    text = stringResource(R.string.home_titulo),
                    color = RosadoNeon,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = onNavigateToCalendar) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = stringResource(R.string.home_abrir_calendario),
                        tint = Color.White
                    )
                }
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
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF25233D)),
                    shape = MaterialTheme.shapes.large,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = stringResource(R.string.home_filtros),
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "${uiState.filteredEvents.size} eventos encontrados",
                                    color = Color.White.copy(alpha = 0.7f),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            if (hasActiveFilters) {
                                TextButton(onClick = viewModel::clearFilters) {
                                    Text("Limpiar", color = RosadoNeon)
                                }
                            }
                        }

                        Text(
                            text = stringResource(R.string.create_categoria),
                            color = Color.White,
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(availableCategories, key = { it.lowercase() }) { categoria ->
                                FilterChip(
                                    selected = uiState.filters.categoria.equals(categoria, ignoreCase = true),
                                    onClick = {
                                        viewModel.updateFilters { it.copy(categoria = categoria) }
                                    },
                                    label = { Text(categoria) }
                                )
                            }
                        }

                        cajasTexto(
                            value = uiState.filters.ciudad,
                            onValueChange = { city ->
                                viewModel.updateFilters { it.copy(ciudad = city) }
                            },
                            label = stringResource(R.string.home_filtrar_ciudad)
                        )

                        Text(
                            text = stringResource(R.string.create_modalidad),
                            color = Color.White,
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(modalityOptions, key = { it }) { modalidad ->
                                FilterChip(
                                    selected = uiState.filters.modalidad.equals(modalidad, ignoreCase = true),
                                    onClick = {
                                        viewModel.updateFilters { it.copy(modalidad = modalidad) }
                                    },
                                    label = {
                                        Text(
                                            modalidad.replaceFirstChar { char ->
                                                if (char.isLowerCase()) char.titlecase() else char.toString()
                                            }
                                        )
                                    }
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FilterChip(
                                selected = uiState.filters.soloGratis,
                                onClick = {
                                    viewModel.updateFilters { it.copy(soloGratis = !it.soloGratis) }
                                },
                                label = { Text(stringResource(R.string.home_solo_gratis)) }
                            )

                            AssistChip(
                                onClick = {},
                                label = { Text("${uiState.filteredEvents.size} resultados") }
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
            onClick = onAddClick,
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
