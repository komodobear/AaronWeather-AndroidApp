package com.komodobear.aaronweather.ui.screens.mainscreen

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.komodobear.aaronweather.model.ThemeColors
import com.komodobear.aaronweather.ui.composables.Navigation
import com.komodobear.aaronweather.ui.composables.PermissionLauncher
import com.komodobear.aaronweather.viewmodels.WeatherVM
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun MainScreen(
	weatherVM: WeatherVM,
	navController: NavHostController,
) {
	val state = weatherVM.weatherState

	val themeColors = ThemeColors(
		textColor = state.weatherInfo?.currentWeatherData?.weatherType?.textColor
			?: Color.White,
		bgColor = state.weatherInfo?.currentWeatherData?.weatherType?.bgColor
			?: Color.Gray,
		darkBgColor = state.weatherInfo?.currentWeatherData?.weatherType?.darkBgColor
			?: Color.DarkGray,
	)

	val drawerState = rememberDrawerState(DrawerValue.Closed)
	val scope = rememberCoroutineScope()

	PermissionLauncher(weatherVM)

	val bottomBar: @Composable () -> Unit = {
		BottomNavigation(
			backgroundColor = themeColors.darkBgColor
		) {
			BottomNavigationItem(
				icon = {
					Icon(
						Icons.AutoMirrored.Filled.List,
						contentDescription = "Drawer",
						tint = themeColors.textColor,
					)
				},
				onClick = {
					scope.launch {
						drawerState.open()
					}
				},
				selected = true
			)
		}
	}

	ModalNavigationDrawer(
		drawerState = drawerState,
		drawerContent = {
			LocationSearchDrawer(
				onLocationSelected = { latLng ->
					weatherVM.updateLocation(latLng)
					Log.d("MainScreen", "Drawer: ${latLng.latitude}, ${latLng.longitude}")
					scope.launch { drawerState.close() }
				},
				themeColors, state
			)
		},
	) {

		Scaffold(
			bottomBar = bottomBar,
			modifier = Modifier.background(themeColors.bgColor),
		) { paddingValues ->

			PullToRefreshBox(
				state = rememberPullToRefreshState(),
				modifier = Modifier.fillMaxSize(),
				isRefreshing = weatherVM.isRefreshing,
				onRefresh = {
					scope.launch {
						weatherVM.refreshFromPullToRefresh()
					}
				}
			) {
				Box(
					modifier = Modifier
						.padding(paddingValues)
						.fillMaxSize()
				) {
					Navigation(weatherVM, navController)
				}
			}
		}

	}
}

