package com.universidad.parchate.ui.screens.chat

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.PermissionChecker
import androidx.lifecycle.viewmodel.compose.viewModel
import com.universidad.parchate.R
import com.universidad.parchate.ui.theme.BackgroundPrincipal
import com.universidad.parchate.ui.theme.RosadoNeon
import com.universidad.parchate.ui.theme.TextoSecundario
import com.universidad.parchate.ui.viewmodel.ChatAuthor
import com.universidad.parchate.ui.viewmodel.ChatMessage
import com.universidad.parchate.ui.viewmodel.ChatbotViewModel

@Composable
fun ChatbotScreen(
    onNavigateBack: () -> Unit = {},
    vm: ChatbotViewModel = viewModel()
) {
    val uiState by vm.uiState.collectAsState()
    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }
    var draftMessage by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current

    // Check and request location permission on first launch
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) vm.fetchUserLocation()
    }

    LaunchedEffect(Unit) {
        val alreadyGranted =
            PermissionChecker.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PermissionChecker.PERMISSION_GRANTED ||
            PermissionChecker.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PermissionChecker.PERMISSION_GRANTED
        if (alreadyGranted) {
            vm.fetchUserLocation()
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    val quickPrompts = listOf(
        stringResource(R.string.chatbot_quick_gratis),
        stringResource(R.string.chatbot_quick_online),
        stringResource(R.string.chatbot_quick_cerca),
        stringResource(R.string.chatbot_quick_bailar),
        stringResource(R.string.chatbot_quick_fin_semana)
    )

    // Scroll to bottom when new messages arrive or typing starts
    LaunchedEffect(uiState.messages.size, uiState.isTyping) {
        val lastIndex = uiState.messages.size - 1 + if (uiState.isTyping) 1 else 0
        if (lastIndex >= 0) listState.animateScrollToItem(lastIndex)
    }

    LaunchedEffect(uiState.error) {
        val error = uiState.error ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(error)
        vm.clearError()
    }

    Scaffold(
        containerColor = BackgroundPrincipal,
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = Color(0xFF3A1A2E),
                    contentColor = Color.White
                )
            }
        },
        bottomBar = {
            ChatInputBar(
                value = draftMessage,
                onValueChange = { draftMessage = it },
                enabled = !uiState.isTyping,
                onSend = {
                    vm.sendMessage(draftMessage)
                    draftMessage = ""
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.atras),
                        tint = RosadoNeon
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.chatbot_titulo),
                        color = Color.White,
                        fontSize = 27.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.chatbot_subtitulo),
                        color = TextoSecundario,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(RosadoNeon.copy(alpha = 0.14f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Forum,
                        contentDescription = null,
                        tint = RosadoNeon
                    )
                }
            }

            // AI + location status card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF282542))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(RosadoNeon.copy(alpha = 0.16f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SmartToy,
                            contentDescription = null,
                            tint = RosadoNeon
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.chatbot_ia_titulo),
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = stringResource(R.string.chatbot_ia_subtitulo),
                            color = TextoSecundario,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    // Location indicator
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = if (uiState.hasLocation) Color(0xFF4CAF50) else TextoSecundario,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = stringResource(R.string.chatbot_sugerencias),
                color = Color.White,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(quickPrompts, key = { it }) { prompt ->
                    AssistChip(
                        onClick = { vm.sendMessage(prompt) },
                        label = { Text(prompt) },
                        enabled = !uiState.isTyping
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(uiState.messages, key = { it.id }) { message ->
                    ChatBubble(message = message)
                }

                if (uiState.isTyping) {
                    item(key = "typing") { TypingIndicator() }
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessage) {
    val isAssistant = message.author == ChatAuthor.Assistant
    val bubbleColor = if (isAssistant) Color(0xFF2A2746) else RosadoNeon
    val bubbleShape = if (isAssistant) {
        RoundedCornerShape(topStart = 8.dp, topEnd = 24.dp, bottomEnd = 24.dp, bottomStart = 24.dp)
    } else {
        RoundedCornerShape(topStart = 24.dp, topEnd = 8.dp, bottomEnd = 24.dp, bottomStart = 24.dp)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isAssistant) Arrangement.Start else Arrangement.End,
        verticalAlignment = Alignment.Bottom
    ) {
        if (isAssistant) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(RosadoNeon.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = null,
                    tint = RosadoNeon,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Surface(
            color = bubbleColor,
            shape = bubbleShape,
            tonalElevation = if (isAssistant) 0.dp else 2.dp,
            modifier = Modifier.widthIn(max = if (isAssistant) 300.dp else 260.dp)
        ) {
            Text(
                text = message.text,
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
            )
        }
    }
}

@Composable
private fun TypingIndicator() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(RosadoNeon.copy(alpha = 0.12f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.SmartToy,
                contentDescription = null,
                tint = RosadoNeon,
                modifier = Modifier.size(18.dp)
            )
        }
        Surface(
            color = Color(0xFF2A2746),
            shape = RoundedCornerShape(topStart = 8.dp, topEnd = 24.dp, bottomEnd = 24.dp, bottomStart = 24.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(14.dp),
                    color = RosadoNeon,
                    strokeWidth = 2.dp
                )
                Text(
                    text = "Parche está pensando…",
                    color = TextoSecundario,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun ChatInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
    onSend: () -> Unit
) {
    Surface(
        color = Color(0xFF171429),
        shadowElevation = 14.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                enabled = enabled,
                placeholder = {
                    Text(
                        text = stringResource(R.string.chatbot_input),
                        color = TextoSecundario
                    )
                },
                shape = RoundedCornerShape(24.dp),
                maxLines = 3,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF25223C),
                    unfocusedContainerColor = Color(0xFF25223C),
                    disabledContainerColor = Color(0xFF1E1B30),
                    focusedIndicatorColor = RosadoNeon,
                    unfocusedIndicatorColor = RosadoNeon.copy(alpha = 0.45f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = RosadoNeon
                )
            )

            Spacer(modifier = Modifier.width(10.dp))

            Surface(
                onClick = onSend,
                shape = CircleShape,
                color = if (enabled) RosadoNeon else RosadoNeon.copy(alpha = 0.4f),
                modifier = Modifier.size(54.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = stringResource(R.string.chatbot_enviar),
                        tint = Color.White
                    )
                }
            }
        }
    }
}
