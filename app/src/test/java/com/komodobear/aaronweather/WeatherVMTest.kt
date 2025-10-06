package com.komodobear.aaronweather

import android.content.Context
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.android.libraries.places.api.net.PlacesClient
import com.komodobear.aaronweather.data.LocationUtilsInterface
import com.komodobear.aaronweather.data.NetworkManagerInterface
import com.komodobear.aaronweather.data.NotificationUtilsInterface
import com.komodobear.aaronweather.model.LocationData
import com.komodobear.aaronweather.model.Result
import com.komodobear.aaronweather.model.weatherdata.WeatherInfo
import com.komodobear.aaronweather.repository.DataStoreRepository
import com.komodobear.aaronweather.repository.GeocodingRepository
import com.komodobear.aaronweather.repository.WeatherRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import junit.framework.TestCase.assertFalse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals


class WeatherVMTest {

	@get:Rule
	val instantTaskExecutorRule = InstantTaskExecutorRule()
	private lateinit var repository: WeatherRepository
	private lateinit var locationUtils: LocationUtilsInterface
	private lateinit var weatherVM: WeatherVM
	private lateinit var context: Context
	private lateinit var placesClient: PlacesClient
	private lateinit var networkManager: NetworkManagerInterface
	private lateinit var geocodingRepository: GeocodingRepository
	private lateinit var notificationUtils: NotificationUtilsInterface
	private lateinit var dataStoreRepository: DataStoreRepository

	@Before
	fun setUp() {
		mockkStatic(Log::class)
		every { Log.d(any(), any()) } returns 0

		repository = mockk()
		locationUtils = mockk()
		placesClient = mockk()
		networkManager = mockk(relaxed = true)
		every { networkManager.isNetworkAvailable } returns MutableStateFlow(true)
		geocodingRepository = mockk()
		notificationUtils = mockk()
		dataStoreRepository = mockk()
		context = mockk(relaxed = true)
		weatherVM = WeatherVM(repository, locationUtils, placesClient, networkManager, geocodingRepository, notificationUtils, dataStoreRepository, context)
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