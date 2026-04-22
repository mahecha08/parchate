package com.universidad.parchate.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Evento(
    @DocumentId
    val id: String = "",
    val titulo: String = "",
    val descripcion: String = "",
    val categoria: String = "",
    val fecha: String = "",
    val hora: String = "",
    val ubicacion: String = "",
    val direccion: String = "",
    val ciudad: String = "",
    val precio: Double? = null,
    val gratis: Boolean = true,
    val modalidad: String = "presencial",
    val organizadorId: String = "",
    val organizadorNombre: String = "",
    val contactoOrganizador: String = "",
    val capacidad: Int? = null,
    val etiquetas: List<String> = emptyList(),
    val imagenUrl: String = "",
    val estado: String = "activo",
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null,
    val destacado: Boolean = false,
    val distanciaKm: Double? = null,
    val latitud: Double? = null,
    val longitud: Double? = null
) {
    fun matchesSearch(query: String): Boolean {
        if (query.isBlank()) return true
        val normalized = query.trim().lowercase()
        return listOf(
            titulo,
            descripcion,
            categoria,
            ciudad,
            ubicacion,
            direccion,
            modalidad,
            organizadorNombre,
            etiquetas.joinToString(" ")
        ).any { it.lowercase().contains(normalized) }
    }
}
