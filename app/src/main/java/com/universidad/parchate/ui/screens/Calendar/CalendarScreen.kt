package com.universidad.parchate.ui.screens.Calendar

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.universidad.parchate.data.model.Evento
import com.universidad.parchate.ui.components.EventCard
import com.universidad.parchate.ui.theme.BackgroundPrincipal
import com.universidad.parchate.ui.theme.RosadoNeon
import com.universidad.parchate.ui.theme.TextoSecundario
import com.universidad.parchate.ui.viewmodel.CalendarViewModel

private enum class CalendarFilter {
    ALL,
    FAVORITES,
    CREATED
}

@Composable
fun CalendarScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: CalendarViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedFilter by remember { mutableStateOf(CalendarFilter.ALL) }

    val eventsToShow = when (selectedFilter) {
        CalendarFilter.ALL -> uiState.allEvents
        CalendarFilter.FAVORITES -> uiState.favoriteEvents
        CalendarFilter.CREATED -> uiState.myEvents
    }

    Scaffold(
        containerColor = BackgroundPrincipal
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(BackgroundPrincipal)
                .padding(horizontal = 16.dp)
        ) {
            CalendarHeader(
                onNavigateBack = onNavigateBack
            )

            CalendarFilterChips(
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it },
                favoritesCount = uiState.favoriteEvents.size,
                createdCount = uiState.myEvents.size,
                allCount = uiState.allEvents.size
            )

            Spacer(modifier = Modifier.height(16.dp))

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = RosadoNeon)
                    }
                }

                uiState.errorMessage != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = uiState.errorMessage.orEmpty(),
                            color = Color.White
                        )
                    }
                }

                eventsToShow.isEmpty() -> {
                    EmptyCalendarState()
                }

                else -> {
                    CalendarEventsList(events = eventsToShow)
                }
            }
        }
    }
}

@Composable
private fun CalendarHeader(
    onNavigateBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp, bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onNavigateBack) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Volver",
                tint = RosadoNeon
            )
        }

        Text(
            text = "Calendario",
            color = RosadoNeon,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
    }

    Text(
        text = "Tus favoritos y eventos creados",
        color = TextoSecundario,
        fontSize = 14.sp,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
private fun CalendarFilterChips(
    selectedFilter: CalendarFilter,
    onFilterSelected: (CalendarFilter) -> Unit,
    favoritesCount: Int,
    createdCount: Int,
    allCount: Int
) {
    val chipColors = FilterChipDefaults.filterChipColors(
        containerColor = Color(0xFF25233D),
        labelColor = Color.White,
        iconColor = Color.White,
        selectedContainerColor = RosadoNeon,
        selectedLabelColor = Color.White,
        selectedLeadingIconColor = Color.White
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedFilter == CalendarFilter.ALL,
            onClick = { onFilterSelected(CalendarFilter.ALL) },
            label = { Text("Todos ($allCount)") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null
                )
            },
            colors = chipColors
        )

        FilterChip(
            selected = selectedFilter == CalendarFilter.FAVORITES,
            onClick = { onFilterSelected(CalendarFilter.FAVORITES) },
            label = { Text("Favoritos ($favoritesCount)") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null
                )
            },
            colors = chipColors
        )

        FilterChip(
            selected = selectedFilter == CalendarFilter.CREATED,
            onClick = { onFilterSelected(CalendarFilter.CREATED) },
            label = { Text("Creados ($createdCount)") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null
                )
            },
            colors = chipColors
        )
    }
}

@Composable
private fun CalendarEventsList(
    events: List<Evento>
) {
    val groupedEvents = events.groupBy { it.fecha }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        groupedEvents.forEach { (date, eventsForDate) ->
            item(key = "date-$date") {
                Text(
                    text = date,
                    color = RosadoNeon,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                )
            }

            items(
                items = eventsForDate,
                key = { event -> event.id }
            ) { event ->
                EventCard(
                    evento = event,
                    onDetailClick = {}
                )
            }
        }
    }
}

@Composable
private fun EmptyCalendarState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No hay eventos para mostrar",
            color = Color.White
        )
    }
}
