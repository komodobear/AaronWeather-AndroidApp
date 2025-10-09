package com.komodobear.aaronweather.usecases

import com.komodobear.aaronweather.model.LocationData
import com.komodobear.aaronweather.model.Result
import com.komodobear.aaronweather.model.weatherdata.WeatherInfo
import com.komodobear.aaronweather.repository.WeatherRepository
import javax.inject.Inject

class WeatherUseCase @Inject constructor(
	private val weatherRepository: WeatherRepository
) {
	suspend operator fun invoke(location: LocationData): Result<WeatherInfo> {
		return weatherRepository.getWeatherData(location.latitude, location.longitude)
	}
}
