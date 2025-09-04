package com.komodobear.aaronweather.weather

data class WeatherInfo(
	val weatherDataPerDay: Map<Int, List<WeatherData>>,
	val currentWeatherData: WeatherData?
)