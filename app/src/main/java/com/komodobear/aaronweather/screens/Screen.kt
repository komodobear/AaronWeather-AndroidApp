package com.komodobear.aaronweather.screens

sealed class Screen(val route: String) {
	object HomeScreen: Screen("home")
	object LoadingScreen: Screen("loading")
}