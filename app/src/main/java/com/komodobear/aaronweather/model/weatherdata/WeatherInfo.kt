package com.komodobear.aaronweather.model.weatherdata

data class WeatherInfo(
	val weatherDataPerDay: Map<Int, List<WeatherData>>,
	val currentWeatherData: WeatherData?
)