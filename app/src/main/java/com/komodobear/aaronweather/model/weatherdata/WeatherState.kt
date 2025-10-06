package com.komodobear.aaronweather.model.weatherdata

data class WeatherState(
	val weatherInfo: WeatherInfo? = null,
	val isLoading: Boolean = false,
	val error: String? = null
)