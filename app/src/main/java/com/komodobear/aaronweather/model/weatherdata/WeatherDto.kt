package com.komodobear.aaronweather.model.weatherdata

import com.squareup.moshi.Json

data class WeatherDto(
	@field:Json(name = "hourly")
    val weatherData: WeatherDataDto,
	@field:Json(name = "timezone")
    val timezone: String
)