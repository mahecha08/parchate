package com.universidad.parchate.ui.screens.chat

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import com.universidad.parchate.R
import com.universidad.parchate.ui.theme.BackgroundPrincipal
import com.universidad.parchate.ui.theme.RosadoNeon
import com.universidad.parchate.ui.theme.TextoSecundario

private enum class ChatAuthor {
    Assistant,
    User
}

private data class ChatPreviewMessage(
    val id: Long,
    val author: ChatAuthor,
    val text: String
)

@Composable
fun ChatbotScreen(
    onNavigateBack: () -> Unit = {}
) {
    val quickPrompts = listOf(
        stringResource(R.string.chatbot_quick_gratis),
        stringResource(R.string.chatbot_quick_online),
        stringResource(R.string.chatbot_quick_cerca),
        stringResource(R.string.chatbot_quick_bailar),
        stringResource(R.string.chatbot_quick_fin_semana)
    )
    val initialMessages = listOf(
        ChatPreviewMessage(
            id = 1L,
            author = ChatAuthor.Assistant,
            text = stringResource(R.string.chatbot_bienvenida)
        ),
        ChatPreviewMessage(
            id = 2L,
            author = ChatAuthor.Assistant,
            text = stringResource(R.string.chatbot_bienvenida_extra)
        )
    )
    val previewGratis = stringResource(R.string.chatbot_preview_gratis)
    val previewOnline = stringResource(R.string.chatbot_preview_online)
    val previewCerca = stringResource(R.string.chatbot_preview_cerca)
    val previewBailar = stringResource(R.string.chatbot_preview_bailar)
    val previewFinDeSemana = stringResource(R.string.chatbot_preview_fin_semana)
    val previewDefault = stringResource(R.string.chatbot_preview_default)
    val messages = remember {
        mutableStateListOf<ChatPreviewMessage>().apply {
            addAll(initialMessages)
        }
    }
    var nextMessageId by remember { mutableStateOf(3L) }
    var draftMessage by rememberSaveable { mutableStateOf("") }

    fun submitPrompt(prompt: String) {
        val cleanPrompt = prompt.trim()
        if (cleanPrompt.isBlank()) return

        messages.add(
            ChatPreviewMessage(
                id = nextMessageId++,
                author = ChatAuthor.User,
                text = cleanPrompt
            )
        )
        messages.add(
            ChatPreviewMessage(
                id = nextMessageId++,
                author = ChatAuthor.Assistant,
                text = previewResponseFor(
                    prompt = cleanPrompt,
                    previewGratis = previewGratis,
                    previewOnline = previewOnline,
                    previewCerca = previewCerca,
                    previewBailar = previewBailar,
                    previewFinDeSemana = previewFinDeSemana,
                    previewDefault = previewDefault
                )
            )
        )
    }

    Scaffold(
        containerColor = BackgroundPrincipal,
        bottomBar = {
            ChatInputBar(
                value = draftMessage,
                onValueChange = { draftMessage = it },
                onSend = {
                    submitPrompt(draftMessage)
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

                    Column {
                        Text(
                            text = "Modo demo",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = stringResource(R.string.chatbot_estado_demo),
                            color = TextoSecundario,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
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
                        onClick = { submitPrompt(prompt) },
                        label = { Text(prompt) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(messages, key = { it.id }) { message ->
                    ChatBubble(message = message)
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(message: ChatPreviewMessage) {
    val isAssistant = message.author == ChatAuthor.Assistant
    val bubbleColor = if (isAssistant) Color(0xFF2A2746) else RosadoNeon
    val textColor = Color.White
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
            modifier = Modifier.fillMaxWidth(if (isAssistant) 0.86f else 0.78f)
        ) {
            Text(
                text = message.text,
                color = textColor,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
            )
        }
    }
}

@Composable
private fun ChatInputBar(
    value: String,
    onValueChange: (String) -> Unit,
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
                color = RosadoNeon,
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

private fun previewResponseFor(
    prompt: String,
    previewGratis: String,
    previewOnline: String,
    previewCerca: String,
    previewBailar: String,
    previewFinDeSemana: String,
    previewDefault: String
): String {
    val normalizedPrompt = prompt.lowercase()
    return when {
        "gratis" in normalizedPrompt -> previewGratis
        "online" in normalizedPrompt || "virtual" in normalizedPrompt -> previewOnline
        "cerca" in normalizedPrompt || "ubic" in normalizedPrompt || "mapa" in normalizedPrompt ->
            previewCerca
        "bail" in normalizedPrompt || "rumba" in normalizedPrompt || "fiesta" in normalizedPrompt ->
            previewBailar
        "fin" in normalizedPrompt || "semana" in normalizedPrompt -> previewFinDeSemana
        else -> previewDefault
    }
}
