package com.komodobear.aaronweather.screens

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import com.komodobear.aaronweather.Screen
import com.komodobear.aaronweather.ThemeColors
import com.komodobear.aaronweather.WeatherVM

@Composable
fun LoadingScreen(
	navController: NavController,
	weatherVM: WeatherVM
) {

	val isNetworkAvailable = weatherVM.isNetworkAvaible.collectAsState()
	val userLocation = weatherVM.userLocation.collectAsState()
	val context = LocalContext.current

	val themeColors = ThemeColors(
		textColor = weatherVM.weatherState.weatherInfo?.currentWeatherData?.weatherType?.textColor
			?: Color.White,
		bgColor = weatherVM.weatherState.weatherInfo?.currentWeatherData?.weatherType?.bgColor
			?: Color.Gray,
		darkBgColor = weatherVM.weatherState.weatherInfo?.currentWeatherData?.weatherType?.bgColor
			?: Color.DarkGray,
	)

	val view = LocalView.current
	val window = (view.context as Activity).window

	SideEffect {
		WindowCompat.getInsetsController(window, view).apply {
			isAppearanceLightStatusBars = false
		}
	}

	LaunchedEffect(Unit) {
		weatherVM.checkNetworkAvailability()
		Log.d("LoadingScreen", "CheckNetworkAvailability")
	}

	LaunchedEffect(userLocation.value) {

		if(! isNetworkAvailable.value) return@LaunchedEffect

		if(userLocation.value == null) {
			Log.d("LoadingScreen", "LoadWeatherInfo")
			weatherVM.loadWeatherInfo(context)
		} else {
			navController.navigate(Screen.HomeScreen.route) {
				Log.d("LoadingScreen", "Navigating to HomeScreen")
				popUpTo(Screen.LoadingScreen.route) {
					inclusive = true
				}
				launchSingleTop = true
			}
		}
	}


	Box(modifier = Modifier
        .fillMaxSize()
		.verticalScroll(rememberScrollState())
        .background(themeColors.bgColor)) {
		if(! isNetworkAvailable.value) {
			Text(
				text = "No internet connection, please check your connection and try again",
				color = themeColors.textColor,
				modifier = Modifier
                    .align(Alignment.Center)
                    .padding(32.dp),
				textAlign = TextAlign.Center,
			)
		} else {
			Column(
				Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(top = 32.dp),
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Text(
					text = "Loading location...",
					fontSize = 20.sp,
					color = themeColors.textColor,
					textAlign = TextAlign.Center,
				)

				Text(
					text = "If you don't want to allow location access, you can still enter your location below",
					modifier = Modifier.padding(48.dp),
					color = themeColors.textColor,
					textAlign = TextAlign.Center
				)
			}
			LoadingAnimation(Modifier
                .align(Alignment.Center)
                .size(130.dp))
		}
	}
}