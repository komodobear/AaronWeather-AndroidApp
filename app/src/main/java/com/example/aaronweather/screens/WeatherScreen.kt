package com.example.aaronweather.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aaronweather.R
import com.example.aaronweather.SystemAppearance
import com.example.aaronweather.ThemeColors
import com.example.aaronweather.WeatherVM
import com.example.aaronweather.weather.WeatherData
import com.example.aaronweather.weather.WeatherState
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
	weatherVM: WeatherVM
) {
	val state = weatherVM.weatherState
	val locationName = weatherVM.locationName

	val themeColors = ThemeColors(
		textColor = state.weatherInfo?.currentWeatherData?.weatherType?.textColor ?: Color.White,
		bgColor = state.weatherInfo?.currentWeatherData?.weatherType?.bgColor ?: Color.Gray,
		darkBgColor = state.weatherInfo?.currentWeatherData?.weatherType?.bgColor ?: Color.DarkGray,
	)

	SystemAppearance(weatherVM)

	state.weatherInfo?.currentWeatherData?.let { data ->

		Column(
			Modifier
				.fillMaxSize()
				.background(themeColors.bgColor)
				.verticalScroll(rememberScrollState())
				.padding(26.dp),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.SpaceBetween
		) {
			Column(
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Text(
					text = locationName.ifEmpty { "Loading..." },
					fontSize = 32.sp,
					fontWeight = FontWeight.Bold,
					textAlign = TextAlign.Center,
					color = themeColors.textColor,
				)

				Spacer(modifier = Modifier.height(8.dp))

				Text(
					text = "Today ${
						data.time.format(
							DateTimeFormatter.ofPattern("HH:mm")
						)
					}",
					fontSize = 18.sp,
					color = themeColors.textColor,
				)

				Spacer(modifier = Modifier.height(20.dp))

				Row(verticalAlignment = Alignment.CenterVertically) {

					Image(
						painter = painterResource(id = data.weatherType.iconRes),
						contentDescription = "Weather icon",
						Modifier.size(100.dp)
					)

					Spacer(modifier = Modifier.width(30.dp))

					Text(
						text = "${data.temperature}°C",
						fontSize = 32.sp,
						fontWeight = FontWeight.Bold,
						color = themeColors.textColor,
					)
				}

				Spacer(modifier = Modifier.height(20.dp))

				Text(
					text = data.weatherType.weatherDesc,
					fontSize = 26.sp,
					color = themeColors.textColor,
				)

				Spacer(modifier = Modifier.height(32.dp))

				Row(
					Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.SpaceAround
				) {
					WeatherDataDisplay(
						value = data.pressure.roundToInt(),
						unit = "hpa",
						icon = ImageVector.vectorResource(id = R.drawable.ic_pressure),
						state = state
					)

					WeatherDataDisplay(
						value = data.humidity.roundToInt(),
						unit = "%",
						icon = ImageVector.vectorResource(id = R.drawable.ic_drop),
						state = state
					)

					WeatherDataDisplay(
						value = data.windSpeed.roundToInt(),
						unit = "km/h",
						icon = ImageVector.vectorResource(id = R.drawable.ic_wind),
						state = state
					)
				}

			}

			val todayData = state.weatherInfo.weatherDataPerDay[0]
			val maxTemp = todayData?.maxOfOrNull { it.temperature } ?: "--"
			val minTemp = todayData?.minOfOrNull { it.temperature } ?: "--"

			Column(
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Row {
					Text(
						text = "Min: $minTemp°C",
						fontSize = 20.sp,
						color = themeColors.textColor,
					)
					Spacer(modifier = Modifier.padding(horizontal = 8.dp))
					Text(
						text = "Max: $maxTemp°C",
						fontSize = 20.sp,
						color = themeColors.textColor,
					)
				}

				HourCard(
					state = state,
					modifier = Modifier
						.padding(vertical = 16.dp)
						.fillMaxWidth()
				)
			}
		}
	}

	if(weatherVM.weatherState.isLoading) {

		Box(
			modifier = Modifier
				.fillMaxSize()
				.background(themeColors.bgColor)
		) {
			Text(
				text = "Loading weather data...",
				modifier = Modifier
					.align(Alignment.TopCenter)
					.padding(top = 32.dp),
				color = themeColors.textColor,
				fontSize = 18.sp,
			)

			CircularProgressIndicator(
				modifier = Modifier
					.align(Alignment.Center)
					.size(36.dp),
				color = themeColors.textColor
			)
		}
	}
	weatherVM.weatherState.error?.let { error ->
		Box(
			modifier = Modifier
				.fillMaxSize()
				.verticalScroll(rememberScrollState())
				.background(themeColors.bgColor)
		) {
			Text(
				text = error,
				fontSize = 16.sp,
				textAlign = TextAlign.Center,
				modifier = Modifier
					.align(Alignment.Center),
				color = themeColors.textColor,
			)
		}
	}
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HourCard(
	state: WeatherState,
	modifier: Modifier = Modifier
) {
	val themeColors = ThemeColors(
		textColor = state.weatherInfo?.currentWeatherData?.weatherType?.textColor ?: Color.White,
		bgColor = state.weatherInfo?.currentWeatherData?.weatherType?.bgColor ?: Color.Gray,
		darkBgColor = state.weatherInfo?.currentWeatherData?.weatherType?.darkBgColor
			?: Color.DarkGray,
	)

	state.weatherInfo?.weatherDataPerDay?.get(0)?.let { data ->

		Card(
			modifier = modifier,
			shape = RoundedCornerShape(16.dp),
			colors = CardDefaults.cardColors(themeColors.darkBgColor.copy(alpha = 0.7f))
		) {
			Column(Modifier.padding(16.dp)) {

				val currentDate = data.firstOrNull()?.time?.format(
					DateTimeFormatter.ofPattern("MMMM, d", Locale.ENGLISH)
				) ?: ""

				Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
					Text("Today", fontSize = 16.sp, color = themeColors.textColor)
					Text(currentDate, fontSize = 16.sp, color = themeColors.textColor)
				}

				Spacer(modifier = Modifier.height(6.dp))

				Divider()

				Spacer(modifier = Modifier.height(12.dp))

				LazyRow(
					horizontalArrangement = Arrangement.SpaceBetween,
					modifier = Modifier.fillMaxWidth()
				) {
					items(data) { weatherData ->
						HourItem(weatherData, state)
					}
				}
			}
		}

	}
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HourItem(
	weatherData: WeatherData,
	state: WeatherState,
) {
	val themeColors = ThemeColors(
		textColor = state.weatherInfo?.currentWeatherData?.weatherType?.textColor ?: Color.White,
		bgColor = state.weatherInfo?.currentWeatherData?.weatherType?.bgColor ?: Color.Gray,
		darkBgColor = state.weatherInfo?.currentWeatherData?.weatherType?.darkBgColor
			?: Color.DarkGray
	)

	val formatedTime = remember(weatherData) {
		weatherData.time.format(
			DateTimeFormatter.ofPattern("HH:mm")
		)
	}

	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.SpaceBetween,
		modifier = Modifier.padding(horizontal = 8.dp)
	) {
		Text("${weatherData.temperature}°C", fontSize = 14.sp, color = themeColors.textColor)
		Spacer(modifier = Modifier.height(4.dp))

		Image(
			painter = painterResource(weatherData.weatherType.iconRes),
			contentDescription = null,
			modifier = Modifier.size(32.dp)
		)

		Spacer(modifier = Modifier.height(4.dp))
		Text(formatedTime, fontSize = 14.sp, color = themeColors.textColor)
	}
}

@Composable
fun WeatherDataDisplay(
	value: Int,
	unit: String,
	icon: ImageVector,
	modifier: Modifier = Modifier,
	textStyle: TextStyle = TextStyle(),
	state: WeatherState
) {
	val themeColors = ThemeColors(
		textColor = state.weatherInfo?.currentWeatherData?.weatherType?.textColor ?: Color.White,
		bgColor = state.weatherInfo?.currentWeatherData?.weatherType?.bgColor ?: Color.Gray,
		darkBgColor = state.weatherInfo?.currentWeatherData?.weatherType?.darkBgColor
			?: Color.DarkGray,
	)

	Row(
		verticalAlignment = Alignment.CenterVertically,
		modifier = modifier
	) {
		Icon(
			imageVector = icon,
			contentDescription = null,
			tint = themeColors.textColor,
			modifier = Modifier.size(26.dp)
		)
		Spacer(modifier = Modifier.width(4.dp))
		Text(
			text = "$value$unit",
			fontSize = 18.sp,
			style = textStyle,
			color = themeColors.textColor
		)
	}
}