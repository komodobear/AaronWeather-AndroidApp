package com.example.aaronweather

import android.app.Activity
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.aaronweather.location.LocationUtils
import com.example.aaronweather.screens.MainScreen
import com.example.aaronweather.ui.theme.WeatherAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity: ComponentActivity() {

	@RequiresApi(Build.VERSION_CODES.O)
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		enableEdgeToEdge()

		setContent {
			val weatherVM: WeatherVM = hiltViewModel()
			val locationUtils = LocationUtils(context = this)
			val navController = rememberNavController()

			WeatherAppTheme(weatherVM = weatherVM) {
				SystemAppearance(weatherVM)
				MainScreen(weatherVM, navController, locationUtils)
			}
		}
	}
}

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