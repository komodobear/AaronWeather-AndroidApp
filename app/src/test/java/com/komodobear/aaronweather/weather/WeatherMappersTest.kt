package com.komodobear.aaronweather.weather

import junit.framework.TestCase.assertEquals
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import kotlin.test.Test
import kotlin.test.assertNotNull

class WeatherMappersTest {

	@Test
	fun `toWeatherDataMap maps DTO correctly`() {
		val dto = WeatherDataDto(
			time = listOf("2023-10-01T00:00", "2023-10-01T01:00"),
			temperatures = listOf(15.0, 14.5),
			weatherCodes = listOf(0, 1),
			pressures = listOf(1015.0, 1014.5),
			windSpeeds = listOf(5.0, 4.5),
			humidities = listOf(80.0, 82.0)
		)
		val map = dto.toWeatherDataMap(ZoneId.of("UTC"))

		assertEquals(1, map.size) // Only one day
		assertEquals(14.5, map[0]?.get(1)?.temperature) // Day one, 1am temperature
	}

	@Test
	fun `toWeatherInfo maps DTO correctly`() {

		val fixedClock = Clock.fixed(
			Instant.parse("2023-10-01T13:00:00Z"),
			ZoneId.of("UTC")
		) // Set fixed time to 2023-10-01 13:00 UTC

		val dto = WeatherDto(
			timezone = "UTC",
			weatherData = WeatherDataDto(
				time = listOf(
					"2023-10-01T11:00:00Z",
					"2023-10-01T12:00:00Z",
					"2023-10-01T13:00:00Z"
				),
				temperatures = listOf(14.0, 15.0, 16.0),
				weatherCodes = listOf(0, 1, 2),
				pressures = listOf(1015.0, 1016.0, 1017.0),
				windSpeeds = listOf(5.0, 6.0, 7.0),
				humidities = listOf(70.0, 75.0, 80.0)
			)
		) // Fake 3 hours of data

		val weatherInfo = dto.toWeatherInfo(fixedClock)
		assertEquals(1, weatherInfo.weatherDataPerDay.size) // One day size map

		val currentWeather = weatherInfo.currentWeatherData
		assertNotNull(currentWeather)
		assertEquals(16.0, currentWeather.temperature) // Current temperature at 13:00
		assertEquals(13, currentWeather.time.hour) // Current hour should be 13:00
	}

}