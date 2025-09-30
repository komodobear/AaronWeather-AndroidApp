package com.komodobear.aaronweather.weather

import com.komodobear.aaronweather.Result
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class WeatherRepositoryImplTest {

	@Test
	fun `getWeatherData returns success when API call is successful`() = runTest {

		val api = mockk<WeatherApi>()
		val dto = WeatherDto(
			weatherData = WeatherDataDto(
				time = listOf("2025-09-22T00:00", "2025-09-22T01:00"),
				temperatures = listOf(14.7, 13.8),
				weatherCodes = listOf(0, 1),
				pressures = listOf(1015.0, 1016.0),
				windSpeeds = listOf(5.0, 4.5),
				humidities = listOf(80.0, 82.0),
			),
			timezone = "UTC+1",
		)
		coEvery { api.getWeatherData(any(), any()) } returns dto // Fake API response

		val repository = WeatherRepositoryImpl(api)
		val result = repository.getWeatherData(1.0, 2.0) // Always returns my dto

		assertTrue(result is Result.Success)
	}
}