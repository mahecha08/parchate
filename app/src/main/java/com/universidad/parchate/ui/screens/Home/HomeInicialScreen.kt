package com.universidad.parchate.ui.screens.Home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.universidad.parchate.data.model.Evento
import com.universidad.parchate.ui.components.EventCard
import com.universidad.parchate.ui.components.cajasTexto
import com.universidad.parchate.ui.theme.*

@Composable
fun HomeScreen(
    onNavigateToCreate: () -> Unit = {},
    //onNavigateToProfile: () -> Unit = {},
    onNavigateToMap: () -> Unit = {}
) {
    var search by remember { mutableStateOf("") }

    val listaEventos = remember {
        listOf(
            Evento("1", "Fiesta Electrónica", "Hoy", "22:00", "Chapinero", "Descripción...", ""),
            Evento("2", "Danza", "Viernes", "16:00", "Teatro Royal", "Descripción...", ""),
            Evento("3", "Torneo Fútbol", "Sábado", "08:00", "Fontibón", "Descripción...", "")
        )
    }

    Scaffold(

        bottomBar = {
            BottomNavParchate(
                onAddClick = onNavigateToCreate,
                //onProfileClick = onNavigateToProfile,
                onMapClick = onNavigateToMap
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
                modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "PARCHATE", color = RosadoNeon, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                cajasTexto(
                    value = search,
                    onValueChange = { search = it },
                    label = "Buscar eventos...",
                    leadingIcon = Icons.Default.Search,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = { /* Acción filtros */ }) {
                    Icon(Icons.Default.Tune, contentDescription = null, tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(listaEventos) { evento ->
                    EventCard(evento = evento, onDetailClick = { /* Ver detalles */ })
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
    onProfileClick: () -> Unit = {}
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
                    contentDescription = "Crear Evento",
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            },
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
        )


        NavigationBarItem(
            selected = false,
            onClick = {},
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