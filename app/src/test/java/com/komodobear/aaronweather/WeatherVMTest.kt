package com.komodobear.aaronweather

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.komodobear.aaronweather.model.LocationData
import com.komodobear.aaronweather.model.Result
import com.komodobear.aaronweather.model.weatherdata.WeatherInfo
import com.komodobear.aaronweather.usecases.FetchLocationNameUseCase
import com.komodobear.aaronweather.usecases.GetLocationUseCase
import com.komodobear.aaronweather.usecases.GetSavedLocationUseCase
import com.komodobear.aaronweather.usecases.LocationPermissionUseCase
import com.komodobear.aaronweather.usecases.NetworkManagerUseCase
import com.komodobear.aaronweather.usecases.NotificationPermissionUseCase
import com.komodobear.aaronweather.usecases.SaveLocationUseCase
import com.komodobear.aaronweather.usecases.UseCasesHolder
import com.komodobear.aaronweather.usecases.WeatherUseCase
import com.komodobear.aaronweather.viewmodels.WeatherVM
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import junit.framework.TestCase.assertFalse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame


class WeatherVMTest {

	@get:Rule
	val instantTaskExecutorRule = InstantTaskExecutorRule()

	private val dispatcher = StandardTestDispatcher()

	private lateinit var useCasesHolder: UseCasesHolder
	private lateinit var weatherVM: WeatherVM


	@OptIn(ExperimentalCoroutinesApi::class)
	@Before
	fun setUp() {

		mockkStatic(Log::class)
		every { Log.d(any(), any()) } returns 0

		Dispatchers.setMain(dispatcher)

		val fetchLocationNameUC = mockk<FetchLocationNameUseCase>()
		val hasNotificationPermissionUC = mockk<NotificationPermissionUseCase>()
		val saveLocationUC = mockk<SaveLocationUseCase>()
		val getSavedLocationUC = mockk<GetSavedLocationUseCase>()
		val networkManagerUC = mockk<NetworkManagerUseCase>()
		val hasLocationPermissionUC = mockk<LocationPermissionUseCase>()
		val getLocationUC = mockk<GetLocationUseCase>()
		val weatherUC = mockk<WeatherUseCase>()

		// fake use cases

		every { hasLocationPermissionUC.invoke() } returns true
		every { hasNotificationPermissionUC.invoke() } returns true
		every { networkManagerUC.isNetworkAvailable } returns MutableStateFlow(true)

		// returns fake values to vm

		useCasesHolder = UseCasesHolder(
			fetchLocationName = fetchLocationNameUC,
			hasNotificationPermission = hasNotificationPermissionUC,
			updateDataStoreLocation = saveLocationUC,
			getSavedLocation = getSavedLocationUC,
			networkState = networkManagerUC,
			hasLocationPermission = hasLocationPermissionUC,
			getLocation = getLocationUC,
			getWeather = weatherUC
		)

		weatherVM = WeatherVM(useCasesHolder)

	}

	@OptIn(ExperimentalCoroutinesApi::class)
	@After
	fun tearDown() {
		Dispatchers.resetMain()
	}


	@OptIn(ExperimentalCoroutinesApi::class)
	@Test
	fun `update location updates userLocation and state`() = runTest {
		// arrange location
		val location = LocationData(latitude = 1.23, longitude = 4.56)

		// arrange state
		val testWeatherInfo = mockk<WeatherInfo>(relaxed = true)

		coEvery { useCasesHolder.fetchLocationName.invoke(location) } returns "Test City"
		coEvery { useCasesHolder.updateDataStoreLocation.invoke(location) } returns Unit
		coEvery { useCasesHolder.getWeather.invoke(location) } returns Result.Success(
			testWeatherInfo
		)

		// test method
		weatherVM.updateLocation(location)
		advanceUntilIdle()

		// assertions
		assertEquals(location, weatherVM.userLocation.value)
		assertEquals("Test City", weatherVM.locationName)
		assertSame(testWeatherInfo, weatherVM.weatherState.weatherInfo)
		assertFalse(weatherVM.isRefreshing)

		// check updateLocation()
		coVerify(exactly = 1) { useCasesHolder.fetchLocationName.invoke(location) }
		coVerify(exactly = 1) { useCasesHolder.updateDataStoreLocation.invoke(location) }
		coVerify(exactly = 1) { useCasesHolder.getWeather.invoke(location) }
	}

}