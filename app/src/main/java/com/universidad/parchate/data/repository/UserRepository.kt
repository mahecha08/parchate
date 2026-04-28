package com.universidad.parchate.data.repository

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.universidad.parchate.data.model.User
import kotlinx.coroutines.tasks.await

data class UpdateProfileRequest(
    val nombres: String,
    val cedula: String,
    val correo: String,
    val fechaNacimiento: String
)

class UserRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val usersCollection = firestore.collection(COLLECTION_USERS)
    private val auth = Firebase.auth

    suspend fun getCurrentUser(): Result<User?> {
        return try {
            val uid = auth.currentUser?.uid ?: return Result.failure(Exception("Usuario no autenticado"))
            val document = usersCollection.document(uid).get().await()
            val user = document.toObject(User::class.java)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun updateProfile(request: UpdateProfileRequest): Result<Boolean> {
        return try {

            val uid = auth.currentUser?.uid ?: return Result.failure(Exception("Usuario no autenticado"))

            val updatedData = User(
                uid = uid,
                nombres = request.nombres.trim(),
                cedula = request.cedula.trim(),
                correo = request.correo.trim(),
                fechaNacimiento = request.fechaNacimiento
            )
            usersCollection.document(uid).set(updatedData).await()

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addFavorite(eventId: String): Result<Boolean> {
        return try {
            val uid = auth.currentUser?.uid ?: return Result.failure(Exception("Usuario no autenticado"))
            val document = usersCollection.document(uid).get().await()
            val user = document.toObject(User::class.java)
            val currentFavorites = user?.favoriteEventIds ?: emptyList()
            if (eventId !in currentFavorites) {
                val updatedFavorites = currentFavorites + eventId
                usersCollection.document(uid).update("favoriteEventIds", updatedFavorites).await()
            }
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeFavorite(eventId: String): Result<Boolean> {
        return try {
            val uid = auth.currentUser?.uid ?: return Result.failure(Exception("Usuario no autenticado"))
            val document = usersCollection.document(uid).get().await()
            val user = document.toObject(User::class.java)
            val currentFavorites = user?.favoriteEventIds ?: emptyList()
            if (eventId in currentFavorites) {
                val updatedFavorites = currentFavorites - eventId
                usersCollection.document(uid).update("favoriteEventIds", updatedFavorites).await()
            }
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        const val COLLECTION_USERS = "users"
    }
}