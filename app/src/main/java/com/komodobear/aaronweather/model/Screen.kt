package com.komodobear.aaronweather.model

sealed class Screen(val route: String) {
	object HomeScreen: Screen("home")
	object LoadingScreen: Screen("loading")
}