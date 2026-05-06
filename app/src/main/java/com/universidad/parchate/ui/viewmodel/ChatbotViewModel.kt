package com.universidad.parchate.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
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

enum class ChatAuthor { User, Assistant }

data class ChatMessage(
    val id: Long,
    val author: ChatAuthor,
    val text: String
)

data class ChatbotUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isTyping: Boolean = false,
    val error: String? = null,
    val hasLocation: Boolean = false
)

@Serializable
private data class EventoResumen(
    val id: String,
    val nombre: String,
    val fecha: String,
    val hora: String,
    val ubicacion: String,
    val ciudad: String,
    val descripcion: String,
    val categoria: String,
    val precio: Double?,
    val gratis: Boolean,
    val modalidad: String,
    val etiquetas: List<String>,
    val latitud: Double?,
    val longitud: Double?
)

@Serializable
private data class ChatApiRequest(
    @SerialName("user_id") val userId: String,
    val message: String,
    val eventos: List<EventoResumen>,
    @SerialName("user_lat") val userLat: Double? = null,
    @SerialName("user_lng") val userLng: Double? = null
)

@Serializable
private data class ChatApiResponse(val reply: String)

class ChatbotViewModel(application: Application) : AndroidViewModel(application) {

    // Emulador: 10.0.2.2 | Dispositivo físico: IP local del PC (ej. 192.168.x.x)
    private val baseUrl = "http://10.0.2.2:8000"

    private val _uiState = MutableStateFlow(ChatbotUiState())
    val uiState: StateFlow<ChatbotUiState> = _uiState.asStateFlow()

    private var nextId = 1L
    private var cachedEvents: List<Evento> = emptyList()
    private var userLat: Double? = null
    private var userLng: Double? = null
    private val userId = Firebase.auth.currentUser?.uid ?: "anon_${System.currentTimeMillis()}"

    private val locationRepository = LocationRepository(application)
    private val eventRepository = EventRepository()

    private val http = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .build()

    private val json = Json { ignoreUnknownKeys = true }

    init {
        addMessage(
            ChatAuthor.Assistant,
            "¡Hola! Soy Parche, tu asistente de Parchate. " +
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
        _uiState.update { it.copy(isTyping = true, error = null) }

        viewModelScope.launch {
            try {
                val reply = callChatApi(clean)
                addMessage(ChatAuthor.Assistant, reply)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "No pude conectarme con el asistente. Verifica que el servidor esté activo.")
                }
            } finally {
                _uiState.update { it.copy(isTyping = false) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private suspend fun callChatApi(message: String): String = withContext(Dispatchers.IO) {
        val eventos = cachedEvents.take(40).map { e ->
            EventoResumen(
                id = e.id,
                nombre = e.nombreVisible,
                fecha = e.fecha,
                hora = e.hora,
                ubicacion = e.ubicacion,
                ciudad = e.ciudad,
                descripcion = e.descripcion.take(200),
                categoria = e.categoria,
                precio = e.precio,
                gratis = e.gratis,
                modalidad = e.modalidad.ifBlank { "presencial" },
                etiquetas = e.etiquetas,
                latitud = e.latitud,
                longitud = e.longitud
            )
        }

        val body = json.encodeToString(
            ChatApiRequest(
                userId = userId,
                message = message,
                eventos = eventos,
                userLat = userLat,
                userLng = userLng
            )
        ).toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("$baseUrl/chat")
            .post(body)
            .build()

        val response = http.newCall(request).execute()
        if (!response.isSuccessful) throw Exception("HTTP ${response.code}")
        val responseBody = response.body?.string() ?: throw Exception("Respuesta vacía")
        json.decodeFromString<ChatApiResponse>(responseBody).reply
    }
}
