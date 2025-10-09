package com.komodobear.aaronweather.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.rememberNavController
import com.komodobear.aaronweather.model.LocationData
import com.komodobear.aaronweather.ui.screens.LoadingScreen
import com.komodobear.aaronweather.viewmodels.WeatherVM
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class LoadingLocationTest {

	@get:Rule
	val composeTestRule = createComposeRule()

	@Test
	fun loadingLocation_noInternetConnection() {

		val fakeWeatherVM = mockk<WeatherVM>(relaxed = true)
		every { fakeWeatherVM.isNetworkAvailable } returns MutableStateFlow(false)
		every { fakeWeatherVM.userLocation } returns MutableStateFlow(LocationData(1.0, 2.0))

		composeTestRule.setContent {
			LoadingScreen(
				navController = rememberNavController(),
				weatherVM = fakeWeatherVM
			)
		}

		composeTestRule.onNodeWithTag("connection error").assertExists().assertTextContains(
			"No internet connection, please check your connection and try again"
		)
	}

	@Test
	fun loadingLocation_noLocation() {

		val fakeWeatherVM = mockk<WeatherVM>(relaxed = true)
		every { fakeWeatherVM.isNetworkAvailable } returns MutableStateFlow(true)
		every { fakeWeatherVM.userLocation } returns MutableStateFlow(null)

		composeTestRule.setContent {
			LoadingScreen(
				navController = rememberNavController(),
				weatherVM = fakeWeatherVM
			)
		}

		composeTestRule.onNodeWithTag("connection error").assertDoesNotExist()
		composeTestRule.onNodeWithText("Loading location...").assertIsDisplayed()

	}

	@Test
	fun loadingLocation_withLocationAndNetwork() {

		val fakeWeatherVM = mockk<WeatherVM>(relaxed = true)
		every { fakeWeatherVM.isNetworkAvailable } returns MutableStateFlow(true)
		every { fakeWeatherVM.userLocation } returns MutableStateFlow(LocationData(1.0, 2.0))
		every { fakeWeatherVM.loadWeatherInfo() } returns Unit

		val navController = mockk<NavHostController>(relaxed = true)
		// mock nav controller to verify navigation

		composeTestRule.setContent {
			LoadingScreen(
				navController = navController,
				weatherVM = fakeWeatherVM
			)
		}

		composeTestRule.onNodeWithTag("connection error").assertDoesNotExist()
		// testing no error message

		verify{
			navController.navigate(any<String>(), any<NavOptionsBuilder.() -> Unit>())
		} // testing navigation occurred

	}
}