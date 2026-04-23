package com.universidad.parchate.ui.screens.Event

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.universidad.parchate.R
import com.universidad.parchate.ui.components.MyEventCard
import com.universidad.parchate.ui.theme.BackgroundPrincipal
import com.universidad.parchate.ui.theme.RosadoNeon
import com.universidad.parchate.ui.viewmodel.MyEventsViewModel

@Composable
fun MyEventsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    viewModel: MyEventsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var eventIdToDelete by remember { mutableStateOf<String?>(null) }


    if (eventIdToDelete != null) {
        AlertDialog(
            onDismissRequest = { eventIdToDelete = null },
            containerColor = Color(0xFF1E1E2F),
            title = { Text(stringResource(R.string.my_events_eliminar_titulo), color = Color.White) },
            text = { Text(stringResource(R.string.my_events_eliminar_mensaje), color = Color.LightGray) },
            confirmButton = {
                TextButton(onClick = {
                    eventIdToDelete?.let { viewModel.deleteEvent(it) }
                    eventIdToDelete = null
                }) {
                    Text(stringResource(R.string.my_events_eliminar), color = RosadoNeon, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { eventIdToDelete = null }) {
                    Text(stringResource(R.string.my_events_cancelar), color = Color.White)
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrincipal)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .background(Color(0xFF25233D))
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    tint = RosadoNeon,
                    modifier = Modifier.size(32.dp)
                )
            }
        }


        Text(
            text = stringResource(R.string.my_events_titulo),
            color = RosadoNeon,
            fontSize = 46.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, bottom = 20.dp),
            textAlign = TextAlign.Center,
            lineHeight = 50.sp
        )

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = RosadoNeon)
            }
        } else if (uiState.events.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = stringResource(R.string.my_events_no_eventos), color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState.events) { evento ->
                    MyEventCard(
                        evento = evento,
                        onEdit = { onNavigateToEdit(evento.id) },
                        onDelete = { eventIdToDelete = evento.id }
                    )
                }
            }
        }
    }
}