package com.komodobear.aaronweather.ui.screens.weatherscreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.komodobear.aaronweather.model.ThemeColors
import com.komodobear.aaronweather.model.weatherdata.WeatherData
import com.komodobear.aaronweather.model.weatherdata.WeatherState
import java.time.format.DateTimeFormatter

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