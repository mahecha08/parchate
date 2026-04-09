package com.universidad.parchate.data.service

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.universidad.parchate.data.model.User
import kotlinx.coroutines.tasks.await

class FirebaseService {

    private val auth = Firebase.auth
    private val db = FirebaseFirestore.getInstance()

    suspend fun registerUser(
        email: String,
        password: String,
        nombres: String,
        cedula: String,
        fechaNacimiento: String
    ): Result<User> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user

            if (user != null) {
                val userData = User(
                    uid = user.uid,
                    nombres = nombres,
                    cedula = cedula,
                    fechaNacimiento = fechaNacimiento,
                    correo = email
                )

                db.collection("users").document(user.uid).set(userData).await()

                Result.success(userData)
            } else {
                Result.failure(Exception("Error al crear el usuario"))
            }
        } catch (e: FirebaseAuthUserCollisionException) {
            Result.failure(Exception("El correo ya está registrado"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
