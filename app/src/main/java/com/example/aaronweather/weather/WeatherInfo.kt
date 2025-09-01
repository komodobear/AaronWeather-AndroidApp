package com.example.aaronweather.weather

data class WeatherInfo(
	val weatherDataPerDay: Map<Int, List<WeatherData>>,
	val currentWeatherData: WeatherData?
)