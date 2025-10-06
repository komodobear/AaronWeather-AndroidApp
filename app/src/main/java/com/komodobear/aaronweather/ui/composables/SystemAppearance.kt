package com.komodobear.aaronweather.ui.composables

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.komodobear.aaronweather.WeatherVM

@Composable
fun SystemAppearance(
	weatherVM: WeatherVM
) {
	val view = LocalView.current
	val window = (view.context as Activity).window
	val darkIcon =
		if(weatherVM.weatherState.weatherInfo?.currentWeatherData?.weatherType?.textColor == Color.White) false else true
	val color =
		weatherVM.weatherState.weatherInfo?.currentWeatherData?.weatherType?.bgColor ?: Color.Gray
	val navColor =
		weatherVM.weatherState.weatherInfo?.currentWeatherData?.weatherType?.darkBgColor ?: Color.DarkGray

	SideEffect {
		WindowCompat.getInsetsController(window, view).apply {
			isAppearanceLightStatusBars = darkIcon
		}
		window.statusBarColor = color.toArgb()
		window.navigationBarColor = navColor.toArgb()
	}
}