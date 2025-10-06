package com.komodobear.aaronweather.data

import android.os.Build
import androidx.annotation.RequiresApi
import com.komodobear.aaronweather.model.weatherdata.WeatherData
import com.komodobear.aaronweather.model.weatherdata.WeatherDataDto
import com.komodobear.aaronweather.model.weatherdata.WeatherDto
import com.komodobear.aaronweather.model.weatherdata.WeatherInfo
import com.komodobear.aaronweather.model.weatherdata.WeatherType
import java.time.Clock
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

private data class IndexedWeatherData(
	val index: Int,
	val data: WeatherData
)

@RequiresApi(Build.VERSION_CODES.O)
fun WeatherDataDto.toWeatherDataMap(timezone: ZoneId): Map<Int, List<WeatherData>> {

	return time.mapIndexed { index, timeStr ->

		val temperature = temperatures[index]
		val weatherCode = weatherCodes[index]
		val pressure = pressures[index]
		val windSpeed = windSpeeds[index]
		val humidity = humidities[index]

		val localDateTime = LocalDateTime.parse(timeStr, DateTimeFormatter.ISO_DATE_TIME)
		val zonedDateTime = ZonedDateTime.of(localDateTime, timezone)

		IndexedWeatherData(
			index = index,
			data = WeatherData(
				time = zonedDateTime,
				temperature = temperature,
				pressure = pressure,
				windSpeed = windSpeed,
				humidity = humidity,
				weatherType = WeatherType.fromWMO(weatherCode)
			)
		)

	}.groupBy {
		it.index / 24
	}.mapValues {
		it.value.map { it.data }
	}
}

@RequiresApi(Build.VERSION_CODES.O)
fun WeatherDto.toWeatherInfo(clock: Clock): WeatherInfo {

	val timezoneId = ZoneId.of(timezone)
	val weatherDataMap = weatherData.toWeatherDataMap(timezoneId)

	val currentTimeInLocation = ZonedDateTime.now(clock).withZoneSameInstant(timezoneId)

	val currentWeatherData = weatherDataMap[0]?.find { data ->
		data.time.toLocalDate() == currentTimeInLocation.toLocalDate() &&
				data.time.hour == currentTimeInLocation.hour
	}

	return WeatherInfo(
		weatherDataPerDay = weatherDataMap,
		currentWeatherData = currentWeatherData
	)
}