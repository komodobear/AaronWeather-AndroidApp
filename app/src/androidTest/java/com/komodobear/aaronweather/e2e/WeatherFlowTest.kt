package com.komodobear.aaronweather.e2e

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.komodobear.aaronweather.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class WeatherFlowTest {

	@get:Rule(order = 0)
	val hiltRule = HiltAndroidRule(this)

	@get:Rule(order = 1)
	val composeRule = createAndroidComposeRule<MainActivity>()

	@Inject
	lateinit var server: MockWebServer

	@Before
	fun setup() {
		hiltRule.inject()

		val geoResponse = """
    {
      "results": [
        {
          "address_components": [
            {
              "long_name": "TestCity",
              "types": ["locality", "political"]
            }
          ]
        }
      ]
    }
    """.trimIndent() //fake response from geocoding API

		server.enqueue(MockResponse().setBody(geoResponse))
		//setting fake geocoding data on MockWebServer

		val weatherResponse = """
{
  "latitude": 1.0,
  "longitude": 2.0,
  "generationtime_ms": 0.16737,
  "utc_offset_seconds": 0,
  "timezone": "UTC",
  "timezone_abbreviation": "UTC",
  "elevation": 113.0,
  "hourly_units": {
    "time": "iso8601",
    "temperature_2m": "°C",
    "weathercode": "wmo code",
    "relativehumidity_2m": "%",
    "windspeed_10m": "km/h",
    "pressure_msl": "hPa"
  },
  "hourly": {
    "time": [
      "2025-01-01T00:00Z",
      "2025-01-01T01:00Z",
      "2025-01-01T02:00Z"
    ],
    "temperature_2m": [
      10.0,
      22.5,
      11.0
    ],
    "weathercode": [0, 1, 0],
    "relativehumidity_2m": [80, 60, 70],
    "windspeed_10m": [5.0, 6.0, 4.0],
    "pressure_msl": [1025.0, 1025.5, 1024.8]
  }
}
""".trimIndent()  //fake response from weather API

		server.enqueue(MockResponse().setBody(weatherResponse))
		//setting fake weather data on MockWebServer
	}

	@After
	fun teardown() {
		try {
			server.shutdown()
		} catch(e: Exception) {
			println("Error shutting down MockWebServer: ${e.message}")
		}
	}

	@Test
	fun testWeatherFlow() {

		//app launches by its own with composeRule

		composeRule.onNodeWithTag("locationName").assertExists().assertTextContains("TestCity")

		composeRule.onNodeWithTag("temperature")
			.assertExists()
			.assertTextContains("10.0°C")

		composeRule.onNodeWithTag("error").assertDoesNotExist()

		composeRule.onNodeWithTag("weatherType")
			.assertExists()
			.assertTextContains("Clear sky")

		composeRule
			.onAllNodesWithText("10.0°C")
			.onFirst()
			.assertIsDisplayed()

		composeRule.onNodeWithText("22.5°C").assertIsDisplayed()
		composeRule.onNodeWithText("11.0°C").assertIsDisplayed()

		composeRule.onNodeWithTag("minTemp").assertTextContains("10.0°C", substring = true)
		composeRule.onNodeWithTag("maxTemp").assertTextContains("22.5°C", substring = true)



		composeRule.onNodeWithText("Loading weather data...", useUnmergedTree = true)
			.assertDoesNotExist()

		// assertions
	}
}