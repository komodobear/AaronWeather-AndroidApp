package com.komodobear.aaronweather.weather

import android.os.Build
import androidx.annotation.RequiresApi
import com.komodobear.aaronweather.Result
import javax.inject.Inject

interface WeatherRepository {
	suspend fun getWeatherData(lat: Double, long: Double): Result<WeatherInfo>
}

class WeatherRepositoryImpl @Inject constructor(
	private val api: WeatherApi
): WeatherRepository {

	@RequiresApi(Build.VERSION_CODES.O)
	override suspend fun getWeatherData(lat: Double, long: Double): Result<WeatherInfo> {
		return try {
			Result.Success(
				data = api.getWeatherData(
					lat = lat,
					long = long
				).toWeatherInfo()
			)
		} catch(e: Exception) {
			e.printStackTrace()
			Result.Error(e.message ?: "An unknown error occurred")
		}
	}

}