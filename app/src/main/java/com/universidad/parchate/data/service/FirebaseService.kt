package com.universidad.parchate.data.service

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.universidad.parchate.data.model.User
import kotlinx.coroutines.tasks.await

private const val TAG = "FirebaseService"

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
            Log.d(TAG, "Creando usuario con email: $email")
            val authResult = auth.createUserWithEmailAndPassword(email.trim(), password.trim()).await()
            val user = authResult.user

            if (user != null) {
                Log.d(TAG, "Usuario Auth creado, uid: ${user.uid}")

                // 2. Enviar correo de verificación (Para validar que es humano/real)
                Log.d(TAG, "Enviando correo de verificación")
                user.sendEmailVerification().await()
                Log.d(TAG, "Correo de verificación enviado")

                // 3. Guardar datos en Firestore
                val userData = User(
                    uid = user.uid,
                    nombres = nombres,
                    cedula = cedula,
                    fechaNacimiento = fechaNacimiento,
                    correo = email.trim()
                )
                Log.d(TAG, "Guardando datos en Firestore, documento: users/${user.uid}")
                db.collection("users").document(user.uid).set(userData).await()
                Log.d(TAG, "Datos guardados exitosamente en Firestore")

                // 4. Cerrar sesión para forzar el login tras verificar
                auth.signOut()
                Log.d(TAG, "Sesión cerrada después del registro")

                Result.success(userData)
            } else {
                Log.e(TAG, "Error: user es null después de createUserWithEmailAndPassword")
                Result.failure(Exception("Error al crear el usuario"))
            }
        } catch (e: FirebaseAuthUserCollisionException) {
            Log.e(TAG, "Error: correo ya registrado", e)
            Result.failure(Exception("El correo ya está registrado"))
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, "Error de Firestore: code=${e.code}, message=${e.message}", e)
            Result.failure(Exception("Error de base de datos: ${e.message}"))
        } catch (e: Exception) {
            Log.e(TAG, "Error desconocido en registro", e)
            Result.failure(e)
        }
    }
}