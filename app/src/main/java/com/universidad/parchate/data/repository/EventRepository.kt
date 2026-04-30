package com.universidad.parchate.data.repository

import android.net.Uri
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.storage
import com.google.firebase.firestore.FieldPath
import com.universidad.parchate.data.model.Evento
import com.universidad.parchate.data.model.toEventoOrNull
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

data class CreateEventRequest(
    val titulo: String,
    val descripcion: String,
    val categoria: String,
    val fecha: String,
    val hora: String,
    val ubicacion: String,
    val direccion: String,
    val ciudad: String,
    val pais: String = "",
    val precio: Double,
    val gratis: Boolean,
    val modalidad: String,
    val organizadorNombre: String,
    val contactoOrganizador: String,
    val capacidad: Int,
    val etiquetas: List<String>,
    val destacado: Boolean,
    val latitud: Double? = null,
    val longitud: Double? = null,
    val imageUri: Uri? = null
)

class EventRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val eventsCollection = firestore.collection(COLLECTION_EVENTS)
    private val storage = Firebase.storage
    private val auth = Firebase.auth

    fun observeActiveEvents(): Flow<Result<List<Evento>>> = callbackFlow {
        val listener = eventsCollection
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }

                val events = snapshot?.documents
                    ?.mapNotNull { document -> document.toEventoOrNull() }
                    ?.filter { it.estado == "activo" || it.estado.isBlank() }
                    ?.sortedWith(compareBy<Evento> { it.fecha }.thenBy { it.hora })
                    .orEmpty()

                trySend(Result.success(events))
            }

        awaitClose { listener.remove() }
    }

    fun observeFavoriteEventIds(userId: String): Flow<Result<List<String>>> = callbackFlow {
        val subscription = firestore
            .collection("users")
            .document(userId)
            .collection("favoritos")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }

                val favoriteIds = snapshot?.documents
                    ?.map { document -> document.id }
                    .orEmpty()

                trySend(Result.success(favoriteIds))
            }

        awaitClose { subscription.remove() }
    }

    suspend fun addFavorite(eventId: String): Result<Boolean> {
        return try {
            val userId = auth.currentUser?.uid
                ?: return Result.failure(Exception("Usuario no autenticado"))

            val data = mapOf(
                "eventId" to eventId,
                "createdAt" to Timestamp.now()
            )

            firestore
                .collection("users")
                .document(userId)
                .collection("favoritos")
                .document(eventId)
                .set(data)
                .await()

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun removeFavorite(eventId: String): Result<Boolean> {
        return try {
            val userId = auth.currentUser?.uid
                ?: return Result.failure(Exception("Usuario no autenticado"))

            firestore
                .collection("users")
                .document(userId)
                .collection("favoritos")
                .document(eventId)
                .delete()
                .await()

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun createEvent(request: CreateEventRequest): Result<String> {
        return try {
            val now = Timestamp.now()
            val docRef = eventsCollection.document()
            val userId = auth.currentUser?.uid.orEmpty()
            val imageUrl = request.imageUri?.let { uri ->
                uploadEventImage(eventId = docRef.id, imageUri = uri)
            }.orEmpty()

            val eventData = buildEventData(
                Evento(
                    id = docRef.id,
                    nombre = request.titulo.trim(),
                    titulo = request.titulo.trim(),
                    fecha = request.fecha.trim(),
                    hora = request.hora.trim(),
                    ubicacion = request.ubicacion.trim(),
                    descripcion = request.descripcion.trim(),
                    imagenUrl = imageUrl,
                    pais = request.pais.trim(),
                    ciudad = request.ciudad.trim(),
                    direccion = request.direccion.trim(),
                    latitud = request.latitud,
                    longitud = request.longitud,
                    estado = "activo",
                    categoria = request.categoria.trim(),
                    contactoOrganizador = request.contactoOrganizador.trim(),
                    precio = if (request.gratis) 0.0 else request.precio,
                    etiquetas = request.etiquetas,
                    modalidad = request.modalidad.trim().lowercase(),
                    organizadorId = userId,
                    organizadorNombre = request.organizadorNombre.trim(),
                    destacado = request.destacado,
                    capacidad = request.capacidad,
                    gratis = request.gratis,
                    createdAt = now,
                    updatedAt = now
                ),
                now = now
            )

            docRef.set(eventData).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getEventById(eventId: String): Result<Evento?> {
        return try {
            val document = eventsCollection.document(eventId).get().await()
            Result.success(document.toEventoOrNull())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateEvent(eventId: String, request: CreateEventRequest): Result<Boolean> {
        return try {
            val updates = mutableMapOf<String, Any>(
                "nombre" to request.titulo.trim(),
                "titulo" to request.titulo.trim(),
                "descripcion" to request.descripcion.trim(),
                "categoria" to request.categoria.trim(),
                "fecha" to request.fecha.trim(),
                "hora" to request.hora.trim(),
                "ubicacion" to request.ubicacion.trim(),
                "direccion" to request.direccion.trim(),
                "ciudad" to request.ciudad.trim(),
                "pais" to request.pais.trim(),
                "precio" to if (request.gratis) 0.0 else request.precio,
                "gratis" to request.gratis,
                "modalidad" to request.modalidad.trim().lowercase(),
                "organizadorNombre" to request.organizadorNombre.trim(),
                "contactoOrganizador" to request.contactoOrganizador.trim(),
                "capacidad" to request.capacidad,
                "etiquetas" to request.etiquetas,
                "destacado" to request.destacado,
                "updatedAt" to Timestamp.now()
            )

            request.latitud?.let { updates["latitud"] = it }
            request.longitud?.let { updates["longitud"] = it }
            request.imageUri?.let { uri ->
                val newImageUrl = uploadEventImage(eventId, uri)
                updates["imagenUrl"] = newImageUrl
            }

            eventsCollection.document(eventId).update(updates).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveEvent(evento: Evento): Result<Unit> {
        return try {
            val now = Timestamp.now()
            val eventData = buildEventData(evento, now)

            if (evento.id.isBlank()) {
                eventsCollection.add(eventData).await()
            } else {
                eventsCollection.document(evento.id).set(eventData).await()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun buildEventData(
        evento: Evento,
        now: Timestamp
    ): HashMap<String, Any?> {
        val eventName = evento.nombreVisible.ifBlank { evento.titulo }
        return hashMapOf(
            "nombre" to eventName,
            "titulo" to eventName,
            "fecha" to evento.fecha,
            "hora" to evento.hora,
            "ubicacion" to evento.ubicacion,
            "descripcion" to evento.descripcion,
            "imagenUrl" to evento.imagenUrl,
            "pais" to evento.pais,
            "ciudad" to evento.ciudad,
            "direccion" to evento.direccion,
            "latitud" to evento.latitud,
            "longitud" to evento.longitud,
            "estado" to if (evento.estado.isBlank()) "activo" else evento.estado,
            "categoria" to evento.categoria,
            "contactoOrganizador" to evento.contactoOrganizador,
            "precio" to if (evento.gratis) 0.0 else evento.precio,
            "etiquetas" to evento.etiquetas,
            "modalidad" to evento.modalidad.lowercase(),
            "organizadorId" to evento.organizadorId,
            "organizadorNombre" to evento.organizadorNombre,
            "distanciaKm" to evento.distanciaKm,
            "destacado" to evento.destacado,
            "capacidad" to evento.capacidad,
            "gratis" to evento.gratis,
            "createdAt" to (evento.createdAt ?: now),
            "updatedAt" to now
        )
    }

    private suspend fun uploadEventImage(eventId: String, imageUri: Uri): String {
        val extension = imageUri.lastPathSegment?.substringAfterLast('.', "jpg") ?: "jpg"
        val imageRef = storage.reference
            .child(COLLECTION_EVENTS)
            .child(eventId)
            .child("${UUID.randomUUID()}.$extension")

        imageRef.putFile(imageUri).await()
        return imageRef.downloadUrl.await().toString()
    }

    fun observeMyEvents(organizadorId: String): Flow<Result<List<Evento>>> = callbackFlow {
        val subscription = eventsCollection
            .whereEqualTo("organizadorId", organizadorId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }

                val events = snapshot?.documents
                    ?.mapNotNull { document -> document.toEventoOrNull() }
                    .orEmpty()

                trySend(Result.success(events))
            }

        awaitClose { subscription.remove() }
    }

    suspend fun deleteEvent(eventId: String): Result<Boolean> {
        return try {
            eventsCollection.document(eventId).delete().await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllEvents(): Result<List<Evento>> {
        return try {
            val snapshot = eventsCollection.get().await()
            val eventos = snapshot.documents.mapNotNull { document ->
                document.toEventoOrNull()
            }
            Result.success(eventos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        const val COLLECTION_EVENTS = "eventos"
    }
}
