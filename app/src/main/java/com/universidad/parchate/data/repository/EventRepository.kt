package com.universidad.parchate.data.repository


import android.net.Uri
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.storage
import com.universidad.parchate.data.model.Evento
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
    val precio: Double,
    val gratis: Boolean,
    val modalidad: String,
    val organizadorNombre: String,
    val contactoOrganizador: String,
    val capacidad: Int,
    val etiquetas: List<String>,
    val destacado: Boolean,
    val imageUri: Uri?
)

class EventRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val eventsCollection = firestore.collection(COLLECTION_EVENTS)
    private val storage = Firebase.storage
    private val auth = Firebase.auth

    fun observeActiveEvents(): Flow<Result<List<Evento>>> = callbackFlow {
        val listener = eventsCollection
            .whereEqualTo("estado", "activo")
            .orderBy("fecha", Query.Direction.ASCENDING)
            .orderBy("hora", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }

                val events = snapshot?.documents
                    ?.mapNotNull { document -> document.toObject(Evento::class.java)?.copy(id = document.id) }
                    .orEmpty()

                trySend(Result.success(events))
            }

        awaitClose { listener.remove() }
    }

    suspend fun createEvent(request: CreateEventRequest): Result<String> {
        return try {
            val userId = auth.currentUser?.uid.orEmpty()
            val docRef = eventsCollection.document()
            val imageUrl = request.imageUri?.let { uri ->
                uploadEventImage(eventId = docRef.id, imageUri = uri)
            }.orEmpty()

            val event = Evento(
                id = docRef.id,
                titulo = request.titulo.trim(),
                descripcion = request.descripcion.trim(),
                categoria = request.categoria.trim(),
                fecha = request.fecha.trim(),
                hora = request.hora.trim(),
                ubicacion = request.ubicacion.trim(),
                direccion = request.direccion.trim(),
                ciudad = request.ciudad.trim(),
                precio = if (request.gratis) 0.0 else request.precio,
                gratis = request.gratis,
                modalidad = request.modalidad.trim(),
                organizadorId = userId,
                organizadorNombre = request.organizadorNombre.trim(),
                contactoOrganizador = request.contactoOrganizador.trim(),
                capacidad = request.capacidad,
                etiquetas = request.etiquetas,
                imagenUrl = imageUrl,
                estado = "activo",
                createdAt = Timestamp.now(),
                updatedAt = Timestamp.now(),
                destacado = request.destacado
            )

            docRef.set(event).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun uploadEventImage(eventId: String, imageUri: Uri): String {
        val extension = imageUri.lastPathSegment?.substringAfterLast('.', "jpg") ?: "jpg"
        val imageRef = storage.reference
            .child("eventos")
            .child(eventId)
            .child("${UUID.randomUUID()}.$extension")

        imageRef.putFile(imageUri).await()
        return imageRef.downloadUrl.await().toString()
    }

    companion object {
        const val COLLECTION_EVENTS = "eventos"
    }
}
