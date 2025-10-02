package com.komodobear.aaronweather

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.komodobear.aaronweather.geocoding.GeocodingRepository
import com.komodobear.aaronweather.location.LocationData
import com.komodobear.aaronweather.location.LocationUtilsInterface
import com.komodobear.aaronweather.weather.WeatherRepository
import com.komodobear.aaronweather.weather.WeatherState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class WeatherVM @Inject constructor(
	private val repository: WeatherRepository,
	private val locationUtils: LocationUtilsInterface,
	private val placesClient: PlacesClient,
	private val networkManager: NetworkManagerInterface,
	private val geocodingRepository: GeocodingRepository,
	@ApplicationContext private val context: Context
): ViewModel() {

	private val _userLocation = MutableStateFlow<LocationData?>(null)
	val userLocation: StateFlow<LocationData?> = _userLocation.asStateFlow()

	val isNetworkAvailable: StateFlow<Boolean> = networkManager.isNetworkAvailable

	var isRefreshing by mutableStateOf(false)
		private set

	var weatherState by mutableStateOf(WeatherState())
		private set

	var locationName by mutableStateOf("")
		private set

	fun updateLocation(newLocation: LocationData) {
		_userLocation.value = newLocation
		fetchLocationName(newLocation)
		fetchWeatherInfo(newLocation)
	}

	fun refreshFromPullToRefresh() {
		if(isRefreshing) return
		viewModelScope.launch {
			isRefreshing = true
			Log.d("WeatherVM", "refreshFromPullToRefresh: Refreshing weather info")
			try {
				loadWeatherInfo(context)
			} finally {
				delay(1000L)
				isRefreshing = false
			}
		}
	}

	fun loadWeatherInfo(context: Context) {
		viewModelScope.launch {
			try {
				weatherState = weatherState.copy(
					isLoading = true,
					error = null
				)
				if(locationUtils.hasLocationPermission(context)) {
					locationUtils.requestLocationUpdates(this@WeatherVM)
				} else {
					weatherState = weatherState.copy(
						isLoading = false,
						error = "Location permission not granted"
					)
					Log.d("WeatherVM", "checkPermission: Location permission not granted")
				}
			} catch(e: Exception) {
				weatherState = weatherState.copy(
					isLoading = false,
					error = "Location error: ${e.message}"
				)
				Log.d("WeatherVM", "loadWeatherInfo exception: ${e.message}")
			}
		}
	}

	fun fetchWeatherInfo(location: LocationData) {
		viewModelScope.launch {
			try {
				when(val result =
					repository.getWeatherData(location.latitude, location.longitude)) {
					is Result.Success -> {
						weatherState = weatherState.copy(
							weatherInfo = result.data,
							isLoading = false,
							error = null
						)
						Log.d("WeatherVM", "fetchWeatherInfo")
					}

					is Result.Error -> {
						weatherState = weatherState.copy(
							weatherInfo = null,
							isLoading = false,
							error = result.message
						)
						Log.d("WeatherVM", "fetchWeatherInfo: ${result.message}")
					}
				}
			} catch(e: Exception) {
				weatherState = weatherState.copy(
					weatherInfo = null,
					isLoading = false,
					error = "Network error: ${e.message}"
				)
				Log.d("WeatherVM", "fetchWeatherInfo exception: ${e.message}")
			}
		}
	}

	private fun fetchLocationName(location: LocationData) {
		viewModelScope.launch {
			val name = geocodingRepository.fetchName(location)
			locationName = name
		}
	}

	fun fetchPredictions(
		query: String,
		onResult: (List<AutocompletePrediction>) -> Unit
	) {
		viewModelScope.launch(Dispatchers.IO) {
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

	fun fetchPlacesDetails(
		placeId: String,
		onResult: (LocationData) -> Unit
	) {
		viewModelScope.launch(Dispatchers.IO) {
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
				Log.d("WeatherVM", "fetchPlacesDetails exception: ${e.message}")
			}
		}
	}

	override fun onCleared() {
		super.onCleared()
		networkManager.cleanup()
	}
}