package com.komodobear.aaronweather

import android.content.Context
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.android.libraries.places.api.net.PlacesClient
import com.komodobear.aaronweather.location.LocationData
import com.komodobear.aaronweather.location.LocationUtils
import com.komodobear.aaronweather.weather.WeatherInfo
import com.komodobear.aaronweather.weather.WeatherRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import junit.framework.TestCase.assertFalse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals


class WeatherVMTest {

	@get:Rule
	val instantTaskExecutorRule = InstantTaskExecutorRule()
	private lateinit var repository: WeatherRepository
	private lateinit var locationUtils: LocationUtils
	private lateinit var weatherVM: WeatherVM
	private lateinit var context: Context
	private lateinit var placesClient: PlacesClient

	@Before
	fun setUp() {
		mockkStatic(Log::class)
		every { Log.d(any(), any()) } returns 0

		repository = mockk()
		locationUtils = mockk()
		placesClient = mockk()
		context = mockk(relaxed = true)
		weatherVM = WeatherVM(repository, locationUtils, placesClient, context)
	}

	@OptIn(ExperimentalCoroutinesApi::class)
	@Test
	fun `update location updates userLocation and state`() {

		val testLocation = LocationData(1.0, 2.0)
		val weatherInfo = WeatherInfo(
			emptyMap(),
			null
		)
		coEvery { repository.getWeatherData(any(), any()) } returns Result.Success(weatherInfo)
		// Fake data fetch

		every { locationUtils.hasLocationPermission(any()) } returns true
		// Location permission granted

		weatherVM.updateLocation(testLocation)

		assertEquals(testLocation, weatherVM.userLocation.value) // Check location updated
		assertEquals(weatherInfo, weatherVM.weatherState.weatherInfo) // Check weather info updated
		assertFalse(weatherVM.weatherState.isLoading) // Check loading state

	}
}