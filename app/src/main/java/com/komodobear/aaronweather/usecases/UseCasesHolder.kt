package com.komodobear.aaronweather.usecases

import javax.inject.Inject

data class UseCasesHolder @Inject constructor(
	val fetchLocationName: FetchLocationNameUseCase,
	val hasNotificationPermission: NotificationPermissionUseCase,
	val updateDataStoreLocation: SaveLocationUseCase,
	val getSavedLocation: GetSavedLocationUseCase,
	val networkState: NetworkManagerUseCase,
	val hasLocationPermission: LocationPermissionUseCase,
	val getLocation: GetLocationUseCase,
	val getWeather: WeatherUseCase
)