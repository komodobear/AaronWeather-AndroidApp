package com.komodobear.aaronweather.model.weatherdata

import com.komodobear.aaronweather.model.weatherdata.WeatherType
import java.time.ZonedDateTime

data class WeatherData(
	val time: ZonedDateTime,
	val temperature: Double,
	val pressure: Double,
	val windSpeed: Double,
	val humidity: Double,
	val weatherType: WeatherType
)