package com.example.aaronweather.weather

data class WeatherState(
	val weatherInfo: WeatherInfo? = null,
	val isLoading: Boolean = false,
	val error: String? = null
)