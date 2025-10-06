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
import com.komodobear.aaronweather.data.LocationUtilsInterface
import com.komodobear.aaronweather.data.NetworkManagerInterface
import com.komodobear.aaronweather.data.NotificationUtilsInterface
import com.komodobear.aaronweather.model.LocationData
import com.komodobear.aaronweather.model.Result
import com.komodobear.aaronweather.model.weatherdata.WeatherState
import com.komodobear.aaronweather.repository.DataStoreRepository
import com.komodobear.aaronweather.repository.GeocodingRepository
import com.komodobear.aaronweather.repository.WeatherRepository
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
	private val notificationUtils: NotificationUtilsInterface,
	private val dataStoreRepository: DataStoreRepository,
	@ApplicationContext private val context: Context
): ViewModel() {

	private val _userLocation = MutableStateFlow<LocationData?>(null)
	val userLocation: StateFlow<LocationData?> = _userLocation.asStateFlow()

	private val _hasLocationPermission = MutableStateFlow(false)
	val hasLocationPermission: StateFlow<Boolean> = _hasLocationPermission.asStateFlow()

	private val _hasNotificationPermission = MutableStateFlow(false)
	val hasNotificationPermission: StateFlow<Boolean> = _hasNotificationPermission.asStateFlow()

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
		updateDataStoreLocation(newLocation)
	}

	fun checkLocationPermission(context: Context) {
		_hasLocationPermission.value = locationUtils.hasLocationPermission(context)
	}

	fun checkNotificationPermission(context: Context) {
		_hasNotificationPermission.value = notificationUtils.hasNotificationPermission(context)
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

	fun updateDataStoreLocation(location: LocationData) {
		viewModelScope.launch {
			dataStoreRepository.saveLocation(location)
		}
	}

	fun loadWeatherInfo(context: Context) {
		viewModelScope.launch {
			try {
				weatherState = weatherState.copy(
					isLoading = true,
					error = null
				)

				if(! locationUtils.hasLocationPermission(context)) {
					weatherState = weatherState.copy(
						isLoading = false,
						error = "Location permission not granted"
					)
					Log.d("WeatherVM", "checkPermission: Location permission not granted")
					return@launch
				}

				val location = withContext(Dispatchers.IO) {
					try {
						locationUtils.getLocation()
					} catch(e: Exception) {
						Log.e("WeatherVM", "getLocation failed: ${e.message}", e)
						null
					}
				}

				if(location != null) {
					updateLocation(location)
				} else {
					weatherState = weatherState.copy(
						isLoading = false,
						error = "Could not obtain location"
					)
					Log.d("WeatherVM", "loadWeatherInfo: Could not obtain location")
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