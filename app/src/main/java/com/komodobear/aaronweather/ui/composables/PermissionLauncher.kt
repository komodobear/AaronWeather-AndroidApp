package com.komodobear.aaronweather.ui.composables

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.komodobear.aaronweather.WeatherVM

@Composable
fun PermissionLauncher(
	weatherVM: WeatherVM
){
	val context = LocalContext.current

	val hasLocationPermission by weatherVM.hasLocationPermission.collectAsState()
	val hasNotificationPermission by weatherVM.hasNotificationPermission.collectAsState()
	var locationPermissionSet by remember { mutableStateOf(false) }

	val notificationLauncher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.RequestPermission()
	) { granted ->
	}

	val locationPermissionLauncher = rememberLauncherForActivityResult(
		ActivityResultContracts.RequestMultiplePermissions()
	) { perms ->
		val fineLocationGranted = perms[Manifest.permission.ACCESS_FINE_LOCATION] == true
		val coarseLocationGranted = perms[Manifest.permission.ACCESS_COARSE_LOCATION] == true

		locationPermissionSet = true

		if(fineLocationGranted || coarseLocationGranted) {
			weatherVM.loadWeatherInfo(context)
		}
	}

	LaunchedEffect(Unit) {
		weatherVM.checkLocationPermission(context)
		if(! hasLocationPermission) {
			locationPermissionLauncher.launch(
				arrayOf(
					Manifest.permission.ACCESS_FINE_LOCATION,
					Manifest.permission.ACCESS_COARSE_LOCATION
				)
			)
		} else {
			weatherVM.loadWeatherInfo(context)
		}
	}

	LaunchedEffect(locationPermissionSet) {
		weatherVM.checkNotificationPermission(context)
		if(! hasNotificationPermission && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)) {
			notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
		}
	}
}