package com.komodobear.aaronweather.weather

import android.os.Build
import androidx.annotation.RequiresApi
import com.komodobear.aaronweather.Resource
import javax.inject.Inject

interface WeatherRepository {
	suspend fun getWeatherData(lat: Double, long: Double): Resource<WeatherInfo>
}

class WeatherRepositoryImpl @Inject constructor(
	private val api: WeatherApi
): WeatherRepository {

	@RequiresApi(Build.VERSION_CODES.O)
	override suspend fun getWeatherData(lat: Double, long: Double): Resource<WeatherInfo> {
		return try {
			Resource.Success(
				data = api.getWeatherData(
					lat = lat,
					long = long
				).toWeatherInfo()
			)
		} catch(e: Exception) {
			e.printStackTrace()
			Resource.Error(e.message ?: "An unknown error occurred")
		}
	}

}