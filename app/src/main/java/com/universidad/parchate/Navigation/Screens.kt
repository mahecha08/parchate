package com.universidad.parchate.Navigation

import kotlinx.serialization.Serializable

@Serializable
object Calendar

@Serializable
object Profile

@Serializable
object EditProfile

@Serializable
object ViewMyEvents

@Serializable
object Favorites

@Serializable
data class UpdateEvent(val eventId: String)
