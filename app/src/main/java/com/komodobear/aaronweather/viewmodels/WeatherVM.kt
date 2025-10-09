package com.komodobear.aaronweather.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.komodobear.aaronweather.model.LocationData
import com.komodobear.aaronweather.model.Result
import com.komodobear.aaronweather.model.weatherdata.WeatherState
import com.komodobear.aaronweather.usecases.UseCasesHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class WeatherVM @Inject constructor(
	private val useCases: UseCasesHolder
): ViewModel() {

	private val _userLocation = MutableStateFlow<LocationData?>(null)
	val userLocation: StateFlow<LocationData?> = _userLocation.asStateFlow()

	private val _hasLocationPermission = MutableStateFlow(false)
	val hasLocationPermission: StateFlow<Boolean> = _hasLocationPermission.asStateFlow()

	private val _hasNotificationPermission = MutableStateFlow(false)
	val hasNotificationPermission: StateFlow<Boolean> = _hasNotificationPermission.asStateFlow()

	var isRefreshing by mutableStateOf(false)
		private set

	var weatherState by mutableStateOf(WeatherState())
		private set

	var locationName by mutableStateOf("")
		private set

	val isNetworkAvailable: StateFlow<Boolean> = useCases.networkState.isNetworkAvailable

	fun updateLocation(newLocation: LocationData) {
		viewModelScope.launch {
			_userLocation.value = newLocation
			fetchLocationName(newLocation)
			fetchWeatherInfo(newLocation)
			updateDataStoreLocation(newLocation)
		}
	}

	fun checkNotificationPermission() {
		_hasNotificationPermission.value =
			useCases.hasNotificationPermission()
	}

	fun checkLocationPermission() {
		_hasLocationPermission.value =
			useCases.hasLocationPermission()
	}

	fun refreshFromPullToRefresh() {
		if(isRefreshing) return
		viewModelScope.launch {
			isRefreshing = true
			Log.d("WeatherVM", "refreshFromPullToRefresh: Refreshing weather info")
			try {
				loadWeatherInfo()
			} finally {
				delay(1000L)
				isRefreshing = false
			}
		}
	}

	fun updateDataStoreLocation(location: LocationData) {
		viewModelScope.launch {
			useCases.updateDataStoreLocation(location)
		}
	}

	fun loadWeatherInfo() {
		viewModelScope.launch {
			try {
				weatherState = weatherState.copy(
					isLoading = true,
					error = null
				)

				if(!useCases.hasLocationPermission()) {
					weatherState = weatherState.copy(
						isLoading = false,
						error = "Location permission not granted"
					)
					Log.d("WeatherVM", "checkPermission: Location permission not granted")
					return@launch
				}

				val location = withContext(Dispatchers.IO) {
					try {
						useCases.getLocation()
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
					useCases.getWeather(location)) {
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

	fun fetchLocationName(locationData: LocationData) {
		viewModelScope.launch {
			locationName = useCases.fetchLocationName(locationData)
		}
	}

	fun startNetworkMonitoring() = useCases.networkState.start()
	fun stopNetworkMonitoring() = useCases.networkState.stop()
}