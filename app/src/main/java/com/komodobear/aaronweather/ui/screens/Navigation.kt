package com.komodobear.aaronweather.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.komodobear.aaronweather.WeatherVM
import com.komodobear.aaronweather.model.Screen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation(
	weatherVM: WeatherVM,
	navController: NavHostController,
) {
	NavHost(
		navController = navController,
		startDestination = Screen.LoadingScreen.route,
	) {
		composable(Screen.LoadingScreen.route) {
			LoadingScreen(navController, weatherVM)
		}

		composable(Screen.HomeScreen.route) {
			WeatherScreen(weatherVM)
		}
	}
}