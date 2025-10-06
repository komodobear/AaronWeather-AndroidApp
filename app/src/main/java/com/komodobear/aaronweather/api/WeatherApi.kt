package com.komodobear.aaronweather.api

import com.komodobear.aaronweather.model.weatherdata.WeatherDto
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi{
    @GET("v1/forecast")
    suspend fun getWeatherData(
		@Query("latitude") lat: Double,
		@Query("longitude") long: Double,
		@Query("timezone") timezone: String = "auto",
		@Query("hourly") hourly: String = "temperature_2m,weathercode,relativehumidity_2m,windspeed_10m,pressure_msl"
    ): WeatherDto
}