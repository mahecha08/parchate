package com.universidad.parchate.ui.screens.Calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.universidad.parchate.data.model.Evento
import com.universidad.parchate.ui.components.EventCard
import com.universidad.parchate.ui.theme.BackgroundPrincipal
import com.universidad.parchate.ui.theme.RosadoNeon
import com.universidad.parchate.ui.theme.TextoSecundario
import com.universidad.parchate.ui.viewmodel.CalendarViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private enum class CalendarFilter { ALL, FAVORITES, CREATED }

private val DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd")

private fun String.toLocalDateOrNull(): LocalDate? = try {
    LocalDate.parse(this.trim(), DATE_FMT)
} catch (_: Exception) { null }

@Composable
fun CalendarScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToDetail: (String) -> Unit = {},
    vm: CalendarViewModel = viewModel()
) {
    val uiState by vm.uiState.collectAsState()
    var selectedFilter by rememberSaveable { mutableStateOf(CalendarFilter.ALL) }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    val filteredEvents = when (selectedFilter) {
        CalendarFilter.ALL       -> uiState.allEvents
        CalendarFilter.FAVORITES -> uiState.favoriteEvents
        CalendarFilter.CREATED   -> uiState.myEvents
    }

    // Fechas con eventos para el mes mostrado (para los puntos del calendario)
    val eventDatesInMonth: Set<LocalDate> = remember(filteredEvents, currentMonth) {
        filteredEvents
            .mapNotNull { it.fecha.toLocalDateOrNull() }
            .filter { it.year == currentMonth.year && it.monthValue == currentMonth.monthValue }
            .toSet()
    }

    // Eventos del día seleccionado (o todos si no hay día seleccionado)
    val eventsToShow = remember(selectedDate, filteredEvents) {
        if (selectedDate == null) filteredEvents
        else filteredEvents.filter { it.fecha.toLocalDateOrNull() == selectedDate }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrincipal)
    ) {
        // ── Header ────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 44.dp, start = 4.dp, end = 16.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = RosadoNeon
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Calendario",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Tus eventos en el tiempo",
                    color = TextoSecundario,
                    fontSize = 13.sp
                )
            }
        }

        // ── Filter chips ──────────────────────────────────────────────────
        val chipColors = FilterChipDefaults.filterChipColors(
            containerColor = Color(0xFF25233D),
            labelColor = Color.White,
            iconColor = Color.White,
            selectedContainerColor = RosadoNeon,
            selectedLabelColor = Color.White,
            selectedLeadingIconColor = Color.White
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedFilter == CalendarFilter.ALL,
                onClick = { selectedFilter = CalendarFilter.ALL; selectedDate = null },
                label = { Text("Todos (${uiState.allEvents.size})") },
                leadingIcon = { Icon(Icons.Default.CalendarMonth, null, Modifier.size(16.dp)) },
                colors = chipColors
            )
            FilterChip(
                selected = selectedFilter == CalendarFilter.FAVORITES,
                onClick = { selectedFilter = CalendarFilter.FAVORITES; selectedDate = null },
                label = { Text("Favoritos (${uiState.favoriteEvents.size})") },
                leadingIcon = { Icon(Icons.Default.Favorite, null, Modifier.size(16.dp)) },
                colors = chipColors
            )
            FilterChip(
                selected = selectedFilter == CalendarFilter.CREATED,
                onClick = { selectedFilter = CalendarFilter.CREATED; selectedDate = null },
                label = { Text("Míos (${uiState.myEvents.size})") },
                leadingIcon = { Icon(Icons.Default.Person, null, Modifier.size(16.dp)) },
                colors = chipColors
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = RosadoNeon)
            }
            return@Column
        }

        // ── Calendario visual ─────────────────────────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF25233D))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                MonthHeader(
                    yearMonth = currentMonth,
                    onPrev = {
                        currentMonth = currentMonth.minusMonths(1)
                        selectedDate = null
                    },
                    onNext = {
                        currentMonth = currentMonth.plusMonths(1)
                        selectedDate = null
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
                WeekDayHeaders()
                Spacer(modifier = Modifier.height(4.dp))
                CalendarGrid(
                    yearMonth = currentMonth,
                    eventDates = eventDatesInMonth,
                    selectedDate = selectedDate,
                    today = LocalDate.now(),
                    onDayClick = { day ->
                        selectedDate = if (selectedDate == day) null else day
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Etiqueta de sección ───────────────────────────────────────────
        val sectionLabel = if (selectedDate != null) {
            val d = selectedDate!!
            val dayName = d.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("es", "CO"))
                .replaceFirstChar { it.titlecase() }
            val monthName = d.month.getDisplayName(TextStyle.FULL, Locale("es", "CO"))
                .replaceFirstChar { it.titlecase() }
            "$dayName ${d.dayOfMonth} de $monthName"
        } else {
            "Todos los eventos"
        }

        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(RosadoNeon, CircleShape)
            )
            Text(
                text = sectionLabel,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            if (eventsToShow.isNotEmpty()) {
                Text(
                    text = "(${eventsToShow.size})",
                    color = TextoSecundario,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ── Lista de eventos ──────────────────────────────────────────────
        if (eventsToShow.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (selectedDate != null) "Sin eventos este día"
                           else "No hay eventos para mostrar",
                    color = TextoSecundario,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
            ) {
                items(eventsToShow, key = { it.id }) { evento ->
                    EventCard(
                        evento = evento,
                        onDetailClick = { onNavigateToDetail(evento.id) }
                    )
                }
            }
        }
    }
}

// ── Componentes del calendario ────────────────────────────────────────────────

@Composable
private fun MonthHeader(
    yearMonth: YearMonth,
    onPrev: () -> Unit,
    onNext: () -> Unit
) {
    val monthName = yearMonth.month
        .getDisplayName(TextStyle.FULL, Locale("es", "CO"))
        .replaceFirstChar { it.titlecase() }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onPrev, modifier = Modifier.size(36.dp)) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                contentDescription = "Mes anterior",
                tint = RosadoNeon,
                modifier = Modifier.size(18.dp)
            )
        }

        Text(
            text = "$monthName ${yearMonth.year}",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        IconButton(onClick = onNext, modifier = Modifier.size(36.dp)) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = "Mes siguiente",
                tint = RosadoNeon,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun WeekDayHeaders() {
    // Empieza en lunes (CO)
    val days = listOf("L", "M", "M", "J", "V", "S", "D")
    Row(modifier = Modifier.fillMaxWidth()) {
        days.forEach { day ->
            Text(
                text = day,
                color = TextoSecundario,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun CalendarGrid(
    yearMonth: YearMonth,
    eventDates: Set<LocalDate>,
    selectedDate: LocalDate?,
    today: LocalDate,
    onDayClick: (LocalDate) -> Unit
) {
    val firstDay = yearMonth.atDay(1)
    // DayOfWeek: MONDAY=1 … SUNDAY=7; offset para que lunes sea columna 0
    val startOffset = (firstDay.dayOfWeek.value - 1)
    val daysInMonth = yearMonth.lengthOfMonth()
    val totalCells = startOffset + daysInMonth
    val rows = (totalCells + 6) / 7

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(rows) { row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                repeat(7) { col ->
                    val cellIndex = row * 7 + col
                    val dayNumber = cellIndex - startOffset + 1

                    if (dayNumber < 1 || dayNumber > daysInMonth) {
                        Spacer(modifier = Modifier.weight(1f))
                    } else {
                        val date = yearMonth.atDay(dayNumber)
                        val hasEvent = date in eventDates
                        val isSelected = date == selectedDate
                        val isToday = date == today

                        DayCell(
                            day = dayNumber,
                            isSelected = isSelected,
                            isToday = isToday,
                            hasEvent = hasEvent,
                            onClick = { onDayClick(date) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    day: Int,
    isSelected: Boolean,
    isToday: Boolean,
    hasEvent: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = when {
        isSelected -> RosadoNeon
        isToday    -> RosadoNeon.copy(alpha = 0.18f)
        else       -> Color.Transparent
    }
    val textColor = when {
        isSelected -> Color.White
        isToday    -> RosadoNeon
        else       -> Color.White
    }
    val borderMod = if (isToday && !isSelected)
        Modifier.border(1.dp, RosadoNeon.copy(alpha = 0.5f), CircleShape)
    else Modifier

    Column(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(CircleShape)
            .background(bgColor, CircleShape)
            .then(borderMod)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = day.toString(),
            color = textColor,
            fontSize = 13.sp,
            fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.Center
        )
        if (hasEvent) {
            Spacer(modifier = Modifier.height(1.dp))
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .background(
                        if (isSelected) Color.White else RosadoNeon,
                        CircleShape
                    )
            )
        }
    }
}
