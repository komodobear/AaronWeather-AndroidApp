package com.komodobear.aaronweather.repository

import android.util.Log
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.komodobear.aaronweather.model.LocationData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface PlacesRepository {
	suspend fun fetchPlacesDetails(
		placeId: String,
		onResult: (LocationData) -> Unit
	)

	suspend fun fetchPredictions(
		query: String,
		onResult: (List<AutocompletePrediction>) -> Unit
	)
}


class PlacesRepositoryImpl @Inject constructor(
	private val placesClient: PlacesClient
): PlacesRepository {
	override suspend fun fetchPlacesDetails(
		placeId: String,
		onResult: (LocationData) -> Unit
	) {
		try {
			val placeFields = listOf(Place.Field.LAT_LNG)
			val request = FetchPlaceRequest.newInstance(placeId, placeFields)

			val response = placesClient.fetchPlace(request).await()
			response.place.latLng?.let { latLng ->
				val location = LocationData(
					latitude = latLng.latitude,
					longitude = latLng.longitude
				)
				withContext(Dispatchers.Main) {
					onResult(location)
				}
			}
		} catch(e: Exception) {
			Log.d("PlacesRepository", "fetchPlacesDetails exception: ${e.message}")
		}

	}

	override suspend fun fetchPredictions(
		query: String,
		onResult: (List<AutocompletePrediction>) -> Unit
	) {

		try {
			val request = FindAutocompletePredictionsRequest.builder()
				.setQuery(query)
				.build()
			val response = placesClient.findAutocompletePredictions(request).await()
			onResult(response.autocompletePredictions)
		} catch(e: Exception) {
			Log.d("WeatherVM", "fetchPredictions failed: ${e.message}")
			withContext(Dispatchers.Main) {
				onResult(emptyList())
			}
		}

	}
}