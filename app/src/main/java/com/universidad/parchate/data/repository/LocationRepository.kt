package com.universidad.parchate.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.universidad.parchate.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume

data class AddressResult(
    val pais: String = "",
    val ciudad: String = "",
    val direccion: String = "",
    val ubicacion: String = ""
)

data class LocationSearchResult(
    val latLng: LatLng,
    val pais: String = "",
    val ciudad: String = "",
    val direccion: String = "",
    val ubicacion: String = ""
) {
    fun asAddressResult(): AddressResult = AddressResult(
        pais = pais,
        ciudad = ciudad,
        direccion = direccion,
        ubicacion = ubicacion
    )
}

data class LocationAutocompleteSuggestion(
    val primaryText: String,
    val secondaryText: String = "",
    val fullText: String = "",
    val placeId: String? = null,
    val fallbackResult: LocationSearchResult? = null
)

class LocationRepository(
    private val context: Context
) {
    private val applicationContext = context.applicationContext

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Location? = suspendCancellableCoroutine { cont ->
        val client = LocationServices.getFusedLocationProviderClient(context)
        client.lastLocation
            .addOnSuccessListener { location -> cont.resume(location) }
            .addOnFailureListener { cont.resume(null) }
    }

    suspend fun resolveAddress(latLng: LatLng): AddressResult {
        return withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale("es", "CO"))
                val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                buildAddressResult(addresses?.firstOrNull())
            } catch (_: Exception) {
                AddressResult()
            }
        }
    }

    suspend fun autocompleteLocations(
        query: String,
        sessionToken: AutocompleteSessionToken? = null,
        maxResults: Int = 5
    ): List<LocationAutocompleteSuggestion> {
        val trimmedQuery = query.trim()
        if (trimmedQuery.isBlank()) return emptyList()

        val placesResults = autocompleteWithPlaces(
            query = trimmedQuery,
            sessionToken = sessionToken
        )
        if (placesResults.isNotEmpty()) return placesResults

        return searchLocations(trimmedQuery, maxResults).map { result ->
            LocationAutocompleteSuggestion(
                primaryText = result.ubicacion.ifBlank { "Lugar encontrado" },
                secondaryText = result.direccion,
                fullText = listOf(result.ubicacion, result.direccion)
                    .filter { it.isNotBlank() }
                    .joinToString(", "),
                fallbackResult = result
            )
        }
    }

    suspend fun resolveAutocompleteSuggestion(
        suggestion: LocationAutocompleteSuggestion
    ): LocationSearchResult? {
        suggestion.fallbackResult?.let { return it }

        val placeId = suggestion.placeId ?: return null
        return fetchPlaceDetails(
            placeId = placeId,
            fallbackPlaceName = suggestion.primaryText
        )
    }

    suspend fun searchLocations(
        query: String,
        maxResults: Int = 5
    ): List<LocationSearchResult> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()

        try {
            val geocoder = Geocoder(context, Locale("es", "CO"))
            val addresses = geocoder.getFromLocationName(query, maxResults).orEmpty()

            addresses.mapNotNull { address ->
                val result = buildAddressResult(address, query)
                if (result.direccion.isBlank() && result.ubicacion.isBlank()) {
                    null
                } else {
                    LocationSearchResult(
                        latLng = LatLng(address.latitude, address.longitude),
                        pais = result.pais,
                        ciudad = result.ciudad,
                        direccion = result.direccion,
                        ubicacion = result.ubicacion.ifBlank { query }
                    )
                }
            }.distinctBy { result ->
                "${result.ubicacion.lowercase()}|${result.direccion.lowercase()}"
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun buildAddressResult(
        address: Address?,
        fallbackPlaceName: String = ""
    ): AddressResult {
        val direccion = address?.getAddressLine(0).orEmpty().ifBlank {
            listOfNotNull(
                address?.thoroughfare?.takeIf { it.isNotBlank() },
                address?.subThoroughfare?.takeIf { it.isNotBlank() },
                address?.locality?.takeIf { it.isNotBlank() }
            ).joinToString(", ")
        }

        val ubicacion = listOfNotNull(
            address?.featureName?.takeIf { it.isNotBlank() },
            address?.premises?.takeIf { it.isNotBlank() },
            address?.subLocality?.takeIf { it.isNotBlank() },
            address?.thoroughfare?.takeIf { it.isNotBlank() },
            fallbackPlaceName.takeIf { it.isNotBlank() }
        ).firstOrNull().orEmpty()

        return AddressResult(
            pais = address?.countryName.orEmpty(),
            ciudad = address?.locality
                ?: address?.subAdminArea
                ?: address?.adminArea
                ?: "",
            direccion = direccion,
            ubicacion = ubicacion.ifBlank { direccion }
        )
    }

    private suspend fun autocompleteWithPlaces(
        query: String,
        sessionToken: AutocompleteSessionToken?
    ): List<LocationAutocompleteSuggestion> {
        val placesClient = getPlacesClientOrNull() ?: return emptyList()

        return try {
            val requestBuilder = FindAutocompletePredictionsRequest.builder()
                .setQuery(query)
                .setInputOffset(query.length)
                .setRegionCode("CO")

            if (sessionToken != null) {
                requestBuilder.setSessionToken(sessionToken)
            }

            val response = placesClient
                .findAutocompletePredictions(requestBuilder.build())
                .await()

            response.autocompletePredictions
                .take(5)
                .map { prediction ->
                    LocationAutocompleteSuggestion(
                        primaryText = prediction.getPrimaryText(null).toString(),
                        secondaryText = prediction.getSecondaryText(null).toString(),
                        fullText = prediction.getFullText(null).toString(),
                        placeId = prediction.placeId
                    )
                }
                .distinctBy { suggestion ->
                    suggestion.placeId ?: suggestion.fullText.lowercase()
                }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private suspend fun fetchPlaceDetails(
        placeId: String,
        fallbackPlaceName: String = ""
    ): LocationSearchResult? {
        val placesClient = getPlacesClientOrNull() ?: return null

        return try {
            val fields = listOf(
                Place.Field.ID,
                Place.Field.DISPLAY_NAME,
                Place.Field.FORMATTED_ADDRESS,
                Place.Field.LOCATION,
                Place.Field.ADDRESS_COMPONENTS
            )

            val request = FetchPlaceRequest.newInstance(placeId, fields)
            val place = placesClient.fetchPlace(request).await().place
            val location = place.location ?: return null
            val address = buildAddressResultFromPlace(place, fallbackPlaceName)

            LocationSearchResult(
                latLng = location,
                pais = address.pais,
                ciudad = address.ciudad,
                direccion = address.direccion,
                ubicacion = address.ubicacion
            )
        } catch (_: Exception) {
            null
        }
    }

    private fun buildAddressResultFromPlace(
        place: Place,
        fallbackPlaceName: String = ""
    ): AddressResult {
        val components = place.addressComponents?.asList().orEmpty()
        val pais = components
            .firstOrNull { component -> "country" in component.types }
            ?.name
            .orEmpty()

        val ciudad = listOf(
            "locality",
            "administrative_area_level_2",
            "administrative_area_level_1",
            "sublocality",
            "neighborhood"
        ).mapNotNull { type ->
            components.firstOrNull { component -> type in component.types }?.name
        }.firstOrNull().orEmpty()

        val direccion = place.formattedAddress.orEmpty()
        val ubicacion = place.displayName
            .orEmpty()
            .ifBlank { fallbackPlaceName.ifBlank { direccion } }

        return AddressResult(
            pais = pais,
            ciudad = ciudad,
            direccion = direccion,
            ubicacion = ubicacion
        )
    }

    private fun getPlacesClientOrNull(): PlacesClient? {
        val apiKey = BuildConfig.PLACES_API_KEY
            .ifBlank { BuildConfig.MAPS_API_KEY }
            .trim()

        if (apiKey.isBlank()) return null

        return try {
            if (!Places.isInitialized()) {
                Places.initializeWithNewPlacesApiEnabled(applicationContext, apiKey)
            }
            Places.createClient(applicationContext)
        } catch (_: Exception) {
            null
        }
    }
}
