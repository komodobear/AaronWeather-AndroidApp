package com.komodobear.aaronweather.screens

import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.komodobear.aaronweather.weather.WeatherData
import com.komodobear.aaronweather.weather.WeatherInfo
import com.komodobear.aaronweather.weather.WeatherState
import com.komodobear.aaronweather.weather.WeatherType
import org.junit.Rule
import org.junit.Test
import java.time.ZonedDateTime

class WeatherScreenTest {

	@get:Rule
	val composeTestRule = createComposeRule()

	@Test
	fun weatherScreen_displayCurrentWeather() {

		val fakeWeatherData = WeatherData(
			time = ZonedDateTime.now(),
			temperature = 22.5,
			pressure = 1013.0,
			windSpeed = 5.0,
			humidity = 60.0,
			weatherType = WeatherType.ClearSky
		)
		val fakeWeatherInfo = WeatherInfo(
			weatherDataPerDay = mapOf(0 to listOf(fakeWeatherData)),
			currentWeatherData = fakeWeatherData
		)
		val fakeState = WeatherState(
			weatherInfo = fakeWeatherInfo,
			isLoading = false,
			error = null
		) // fake viewmodel state

		composeTestRule.setContent {
			WeatherContent(
				state = fakeState,
				locationName = "Test",
			)
		} // setting composable content

		composeTestRule.onNodeWithTag("temperature").assertExists().assertTextContains("22.5", substring = true)
		composeTestRule.onNodeWithTag("locationName").assertExists().assertTextContains("Test")
    	composeTestRule.onNodeWithTag("weatherType").assertExists().assertTextContains("Clear sky")
    	composeTestRule.onNodeWithTag("loading").assertDoesNotExist()
    	composeTestRule.onNodeWithTag("error").assertDoesNotExist()
		// assertions
	}

	@Test
	fun weatherScreen_displayLoadingIndicator() {

		val fakeState = WeatherState(
			weatherInfo = null,
			isLoading = true,
			error = null
		) // fake viewmodel state

		composeTestRule.setContent {
			WeatherContent(
				state = fakeState,
				locationName = "Test",
			)
		} // setting composable content

		composeTestRule.onNodeWithTag("loading").assertExists()
		composeTestRule.onNodeWithTag("temperature").assertDoesNotExist()
		composeTestRule.onNodeWithTag("error").assertDoesNotExist()
		// assertions

	}

	@Test
	fun weatherScreen_displayErrorMessage() {
		val fakeState = WeatherState(
			weatherInfo = null,
			isLoading = false,
			error = "Test"
		) // fake viewmodel state

		composeTestRule.setContent {
			WeatherContent(
				state = fakeState,
				locationName = "Test",
			)
		} // setting composable content

		composeTestRule.onNodeWithTag("error").assertExists().assertTextContains("Test")
		composeTestRule.onNodeWithTag("temperature").assertDoesNotExist()
		composeTestRule.onNodeWithTag("locationName").assertDoesNotExist()
		composeTestRule.onNodeWithTag("loading").assertDoesNotExist()
		// assertions
	}
}