package com.universidad.parchate.Navigation

import kotlinx.serialization.Serializable

@Serializable
object Inicio

@Serializable
object Login

@Serializable
object Home

@Serializable
object Register

@Serializable
object ForgotPassword

@Serializable
object Calendar

@Serializable
data class VerificationCode(
    val method: String,   // "email" o "phone"
    val contact: String   // correo o número de teléfono
)

@Serializable
object  CreateEvent

@Serializable
object Profile

@Serializable
object EditProfile

@Serializable
object ViewMyEvents


@Serializable
data class UpdateEvent(val eventId: String)