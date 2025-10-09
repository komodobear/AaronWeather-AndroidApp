package com.komodobear.aaronweather

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.komodobear.aaronweather.data.LocationUtils
import com.komodobear.aaronweather.ui.composables.SystemAppearance
import com.komodobear.aaronweather.ui.screens.mainscreen.MainScreen
import com.komodobear.aaronweather.ui.theme.WeatherAppTheme
import com.komodobear.aaronweather.viewmodels.PlacesVM
import com.komodobear.aaronweather.viewmodels.WeatherVM
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity: ComponentActivity() {

	@Inject
	lateinit var locationUtils: LocationUtils

	@RequiresApi(Build.VERSION_CODES.O)
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		enableEdgeToEdge()

		setContent {
			val weatherVM: WeatherVM = hiltViewModel()
			val placesVM: PlacesVM = hiltViewModel()
			val navController = rememberNavController()

			WeatherAppTheme(weatherVM = weatherVM) {
				SystemAppearance(weatherVM)
				MainScreen(weatherVM, navController)
			}
		}
	}
}