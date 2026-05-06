package com.universidad.parchate.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.universidad.parchate.data.model.Evento
import com.universidad.parchate.data.repository.EventRepository
import com.universidad.parchate.data.repository.LocationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

enum class ChatAuthor { User, Assistant }

data class ChatMessage(
    val id: Long,
    val author: ChatAuthor,
    val text: String
)

data class ChatbotUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isTyping: Boolean = false,
    val hasLocation: Boolean = false
)

@Serializable
private data class GroqMessage(val role: String, val content: String)

@Serializable
private data class GroqRequest(
    val model: String,
    val messages: List<GroqMessage>,
    val temperature: Double = 0.7,
    @SerialName("max_tokens") val maxTokens: Int = 1024
)

@Serializable
private data class GroqChoice(val message: GroqMessage)

@Serializable
private data class GroqResponse(val choices: List<GroqChoice>)

class ChatbotViewModel(application: Application) : AndroidViewModel(application) {

    // Obtén tu key gratis en console.groq.com → Create API Key
    private val groqApiKey = "key aca"
    private val groqUrl = "https://api.groq.com/openai/v1/chat/completions"
    private val groqModel = "llama-3.1-8b-instant"

    private val _uiState = MutableStateFlow(ChatbotUiState())
    val uiState: StateFlow<ChatbotUiState> = _uiState.asStateFlow()

    private var nextId = 1L
    private var cachedEvents: List<Evento> = emptyList()
    private var userLat: Double? = null
    private var userLng: Double? = null
    private val history = mutableListOf<GroqMessage>()

    private val locationRepository = LocationRepository(application)
    private val eventRepository = EventRepository()

    private val http = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    private val json = Json { ignoreUnknownKeys = true }

    init {
        addMessage(
            ChatAuthor.Assistant,
            "¡Hola! Soy CompaIA, tu asistente de Parchate. " +
                "Cuéntame qué tipo de plan buscas y te ayudo a encontrarlo."
        )
        observeEvents()
    }

    private fun addMessage(author: ChatAuthor, text: String) {
        _uiState.update { state ->
            state.copy(
                messages = state.messages + ChatMessage(id = nextId++, author = author, text = text)
            )
        }
    }

    private fun observeEvents() {
        viewModelScope.launch {
            eventRepository.observeActiveEvents().collect { result ->
                result.onSuccess { events -> cachedEvents = events }
            }
        }
    }

    fun fetchUserLocation() {
        viewModelScope.launch {
            val location = locationRepository.getCurrentLocation() ?: return@launch
            userLat = location.latitude
            userLng = location.longitude
            _uiState.update { it.copy(hasLocation = true) }
        }
    }

    fun sendMessage(text: String) {
        val clean = text.trim()
        if (clean.isBlank() || _uiState.value.isTyping) return

        addMessage(ChatAuthor.User, clean)
        _uiState.update { it.copy(isTyping = true) }

        viewModelScope.launch {
            try {
                val reply = callGroq(clean)
                addMessage(ChatAuthor.Assistant, reply)
            } catch (e: Exception) {
                addMessage(
                    ChatAuthor.Assistant,
                    "⚠️ No pude conectarme. Verifica tu conexión a internet e intenta de nuevo."
                )
            } finally {
                _uiState.update { it.copy(isTyping = false) }
            }
        }
    }

    private fun haversineKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371.0
        val dlat = Math.toRadians(lat2 - lat1)
        val dlon = Math.toRadians(lon2 - lon1)
        val a = sin(dlat / 2).pow(2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dlon / 2).pow(2)
        return R * 2 * asin(sqrt(a))
    }

    private fun buildSystemPrompt(): String {
        val sb = StringBuilder()
        sb.append(
            "Eres Parche, el asistente IA de Parchate — una app colombiana para descubrir " +
                "eventos culturales, deportivos, musicales y de entretenimiento.\n\n" +
                "Tu rol es ayudar a los usuarios a encontrar el plan perfecto basándote " +
                "ÚNICAMENTE en los eventos reales listados a continuación.\n" +
                "- No inventes eventos ni fechas.\n" +
                "- Si no hay eventos que coincidan, dilo amablemente.\n" +
                "- Cuando recomiendes un evento incluye: nombre, fecha, hora, lugar, precio y distancia.\n" +
                "- Responde siempre en español, de forma amigable y concisa (máximo 3-4 eventos).\n\n"
        )

        val eventos = cachedEvents.take(40)
        if (eventos.isEmpty()) {
            sb.append("[No hay eventos disponibles en este momento.]")
            return sb.toString()
        }

        sb.append("=== EVENTOS DISPONIBLES EN PARCHATE ===\n")
        val hasLoc = userLat != null && userLng != null

        val sorted = eventos
            .map { e ->
                val dist = if (hasLoc && e.latitud != null && e.longitud != null)
                    haversineKm(userLat!!, userLng!!, e.latitud, e.longitud) else null
                Pair(dist, e)
            }
            .sortedWith(compareBy({ it.first == null }, { it.first ?: 0.0 }))

        for ((dist, e) in sorted) {
            val precio = if (e.gratis) "Gratis" else e.precio?.let { "${"%.0f".format(it)}" } ?: "Gratis"
            val distStr = dist?.let { "${"%.1f".format(it)} km de ti" } ?: "distancia desconocida"
            sb.append(
                "• ${e.nombreVisible} | ${e.categoria} | ${e.fecha} ${e.hora} | " +
                    "${e.ubicacion}, ${e.ciudad} | $precio | " +
                    "${e.modalidad.ifBlank { "presencial" }} | $distStr\n"
            )
            if (e.descripcion.isNotBlank()) sb.append("  ${e.descripcion.take(150)}\n")
        }
        sb.append("=== FIN DE EVENTOS ===")
        return sb.toString()
    }

    private suspend fun callGroq(message: String): String = withContext(Dispatchers.IO) {
        history.add(GroqMessage("user", message))

        val messages = mutableListOf(GroqMessage("system", buildSystemPrompt()))
        messages += history.takeLast(12)

        val body = json.encodeToString(
            GroqRequest(model = groqModel, messages = messages)
        ).toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(groqUrl)
            .addHeader("Authorization", "Bearer $groqApiKey")
            .post(body)
            .build()

        val response = http.newCall(request).execute()
        if (!response.isSuccessful) {
            val errorBody = response.body?.string() ?: "sin cuerpo"
            throw Exception("HTTP ${response.code}: $errorBody")
        }
        val responseBody = response.body?.string() ?: throw Exception("Respuesta vacía")
        val reply = json.decodeFromString<GroqResponse>(responseBody).choices.first().message.content

        history.add(GroqMessage("assistant", reply))
        if (history.size > 20) repeat(history.size - 20) { history.removeAt(0) }

        reply
    }
}
