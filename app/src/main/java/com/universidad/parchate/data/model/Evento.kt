package com.universidad.parchate.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Evento(
    @DocumentId
    val id: String = "",
    val nombre: String = "",
    val titulo: String = "",
    val fecha: String = "",
    val hora: String = "",
    val ubicacion: String = "",
    val descripcion: String = "",
    val imagenUrl: String = "",
    val pais: String = "",
    val ciudad: String = "",
    val direccion: String = "",
    val latitud: Double? = null,
    val longitud: Double? = null,
    val estado: String = "activo",
    val categoria: String = "",
    val contactoOrganizador: String = "",
    val precio: Double? = null,
    val etiquetas: List<String> = emptyList(),
    val modalidad: String = "presencial",
    val organizadorId: String = "",
    val organizadorNombre: String = "",
    val distanciaKm: Double? = null,
    val destacado: Boolean = false,
    val capacidad: Int? = null,
    val gratis: Boolean = true,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
) {
    val nombreVisible: String
        get() = nombre.ifBlank { titulo }
}

fun DocumentSnapshot.toEventoOrNull(): Evento? {
    val evento = toObject(Evento::class.java) ?: return null
    val nombreNormalizado = evento.nombre.ifBlank {
        evento.titulo.ifBlank { getString("titulo").orEmpty() }
    }

    return evento.copy(
        id = id,
        nombre = nombreNormalizado,
        titulo = evento.titulo.ifBlank { nombreNormalizado }
    )
}
