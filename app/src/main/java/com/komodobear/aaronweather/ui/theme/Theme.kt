package com.komodobear.aaronweather.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.komodobear.aaronweather.WeatherVM

private var DarkColorScheme = darkColorScheme(
	primary = pink,
	secondary = lightPink,
	tertiary = gray
)

private var LightColorScheme = lightColorScheme(
	primary = Color.DarkGray,
	secondary = Color.DarkGray,
	background = Color.Gray,
)

@Composable
fun WeatherAppTheme(
	darkTheme: Boolean = false, //maybe next update
	dynamicColor: Boolean = false,
	weatherVM: WeatherVM,
	content: @Composable () -> Unit
) {

	LightColorScheme = lightColorScheme(
		background = weatherVM.weatherState.weatherInfo?.currentWeatherData?.weatherType?.bgColor
			?: Color.Gray,
	)

	val colorScheme = when {

		dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
			val context = LocalContext.current
			if(darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
		}

		darkTheme -> DarkColorScheme
		else -> LightColorScheme
	}

	MaterialTheme(
		colorScheme = colorScheme,
		typography = Typography,
		content = content
	)
}