package com.example.aaronweather

import android.content.Context
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aaronweather.location.LocationData
import com.example.aaronweather.location.LocationUtils
import com.example.aaronweather.weather.WeatherRepository
import com.example.aaronweather.weather.WeatherState
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
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
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class WeatherVM @Inject constructor(
	private val repository: WeatherRepository,
	private val locationUtils: LocationUtils,
	@ApplicationContext private val context: Context
): ViewModel() {

	private val _userLocation = MutableStateFlow<LocationData?>(null)
	val userLocation: StateFlow<LocationData?> = _userLocation.asStateFlow()

	private val _isNetworkAvailable = MutableStateFlow(false)
	val isNetworkAvaible: StateFlow<Boolean> = _isNetworkAvailable.asStateFlow()

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

	private fun fetchLocationName(location: LocationData) {
		viewModelScope.launch(Dispatchers.IO) {
			try {
				val geocoder = Geocoder(context, Locale.getDefault())
				val addresses = geocoder.getFromLocation(
					location.latitude,
					location.longitude,
					1
				)
				addresses?.firstOrNull()?.let { address ->
					val cityName = address.locality ?: address.subAdminArea ?: "Unknown"
					withContext(Dispatchers.Main) {
						locationName = cityName
					}
				}

			} catch(e: Exception) {
				Log.d("WeatherVM", "Geocoder error: ${e.message}")
			}
		}
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
					is Resource.Success -> {
						weatherState = weatherState.copy(
							weatherInfo = result.data,
							isLoading = false,
							error = null
						)
						Log.d("WeatherVM", "fetchWeatherInfo")
					}

					is Resource.Error -> {
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

	fun fetchPredictions(
		client: PlacesClient,
		query: String,
		onResult: (List<AutocompletePrediction>) -> Unit
	) {
		viewModelScope.launch(Dispatchers.IO) {
			try {
				val request = FindAutocompletePredictionsRequest.builder()
					.setQuery(query)
					.build()
				val response = client.findAutocompletePredictions(request).await()
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
		client: PlacesClient,
		placeId: String,
		onResult: (LocationData) -> Unit
	) {
		viewModelScope.launch(Dispatchers.IO) {
			try {
				val placeFields = listOf(Place.Field.LAT_LNG)
				val request = FetchPlaceRequest.newInstance(placeId, placeFields)

				val response = client.fetchPlace(request).await()
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

	private val conectivityManager: ConnectivityManager by lazy {
		context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
	}

	private val networkCallback = object: ConnectivityManager.NetworkCallback() {
		override fun onAvailable(network: Network) {
			Log.d("WeatherVM", "onAvailable: Network available")
			_isNetworkAvailable.value = true
		}

		override fun onLost(network: Network) {
			Log.d("WeatherVM", "onLost: Network lost")
			_isNetworkAvailable.value = false
		}
	}

	init {
		try {
			val request = NetworkRequest.Builder()
				.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
				.build()
			conectivityManager.registerNetworkCallback(request, networkCallback)
			checkNetworkAvailability()
			Log.d("WeatherVM", "init: Network callback registered")
		} catch(e: Exception) {
			Log.d("WeatherVM", "init: Network callback registration failed: ${e.message}")
			_isNetworkAvailable.value = false
		}
	}

	override fun onCleared() {
		super.onCleared()
		conectivityManager.unregisterNetworkCallback(networkCallback)
	}

	fun checkNetworkAvailability() {
		val network = conectivityManager.activeNetwork
		val capabilities = conectivityManager.getNetworkCapabilities(network)
		_isNetworkAvailable.value = capabilities != null &&
				capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
	}
}