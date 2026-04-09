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
            // 1. Crear usuario en Firebase Auth
            val authResult = auth.createUserWithEmailAndPassword(email.trim(), password.trim()).await()
            val user = authResult.user

            if (user != null) {
                // 2. Enviar correo de verificación (Para validar que es humano/real)
                user.sendEmailVerification().await()

                // 3. Guardar datos en Firestore
                val userData = User(
                    uid = user.uid,
                    nombres = nombres,
                    cedula = cedula,
                    fechaNacimiento = fechaNacimiento,
                    correo = email.trim()
                )
                db.collection("users").document(user.uid).set(userData).await()

                // 4. Cerrar sesión para forzar el login tras verificar
                auth.signOut()

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