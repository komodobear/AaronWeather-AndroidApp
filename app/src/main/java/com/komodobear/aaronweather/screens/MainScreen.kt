package com.komodobear.aaronweather.screens

import android.Manifest
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.komodobear.aaronweather.ThemeColors
import com.komodobear.aaronweather.WeatherVM
import com.komodobear.aaronweather.location.LocationData
import com.komodobear.aaronweather.location.LocationUtilsInterface
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(
	weatherVM: WeatherVM,
	navController: NavHostController,
	locationUtils: LocationUtilsInterface,
) {
	val themeColors = ThemeColors(
		textColor = weatherVM.weatherState.weatherInfo?.currentWeatherData?.weatherType?.textColor
			?: Color.White,
		bgColor = weatherVM.weatherState.weatherInfo?.currentWeatherData?.weatherType?.bgColor
			?: Color.Gray,
		darkBgColor = weatherVM.weatherState.weatherInfo?.currentWeatherData?.weatherType?.darkBgColor
			?: Color.DarkGray,
	)

	val drawerState = rememberDrawerState(DrawerValue.Closed)
	val scope = rememberCoroutineScope()
	val context = LocalContext.current

	val isRefreshing = weatherVM.isRefreshing

	val hasLocationPermission = locationUtils.hasLocationPermission(context)

	val permissionLauncher = rememberLauncherForActivityResult(
		ActivityResultContracts.RequestMultiplePermissions()
	) { perms ->
		val fineLocationGranted = perms[Manifest.permission.ACCESS_FINE_LOCATION] == true
		val coarseLocationGranted = perms[Manifest.permission.ACCESS_COARSE_LOCATION] == true

		if(fineLocationGranted || coarseLocationGranted) {
			locationUtils.requestLocationUpdates(weatherVM)
		}
	}

	LaunchedEffect(Unit) {
		if(!hasLocationPermission) {
			permissionLauncher.launch(
				arrayOf(
					Manifest.permission.ACCESS_FINE_LOCATION,
					Manifest.permission.ACCESS_COARSE_LOCATION
				)
			)
		}else{
			locationUtils.requestLocationUpdates(weatherVM)
		}
	}

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
				weatherVM
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
				isRefreshing = isRefreshing,
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


@Composable
fun LocationSearchDrawer(
	onLocationSelected: (LocationData) -> Unit,
	weatherVM: WeatherVM
) {
	val themeColors = ThemeColors(
		textColor = weatherVM.weatherState.weatherInfo?.currentWeatherData?.weatherType?.textColor
			?: Color.White,
		bgColor = weatherVM.weatherState.weatherInfo?.currentWeatherData?.weatherType?.bgColor
			?: Color.Gray,
		darkBgColor = weatherVM.weatherState.weatherInfo?.currentWeatherData?.weatherType?.darkBgColor
			?: Color.DarkGray,
	)

	var searchText by remember { mutableStateOf("") }
	var predictions by remember { mutableStateOf<List<AutocompletePrediction>>(emptyList()) }

	ModalDrawerSheet(
		drawerContainerColor = themeColors.darkBgColor
	) {
		Column(
			Modifier.padding(16.dp)
		) {
			TextField(
				value = searchText,
				onValueChange = { text ->
					searchText = text
					when {
						text.length < 2 -> {
							predictions = emptyList()
						}

						text.length >= 2 -> {
							weatherVM.fetchPredictions( text) { results ->
								predictions = results
							}
						}
					}
				},
				colors = TextFieldDefaults.textFieldColors(
					textColor = themeColors.textColor,
					focusedIndicatorColor = themeColors.textColor,
					cursorColor = themeColors.textColor,
				),
				label = { Text("Search location", color = themeColors.textColor) },
				leadingIcon = {
					Icon(
						Icons.Default.Search,
						contentDescription = "Search",
						tint = themeColors.textColor
					)
				},
				modifier = Modifier.fillMaxWidth()
			)

			when {

				weatherVM.weatherState.isLoading && searchText.length <= 2 -> {
					Spacer(modifier = Modifier.size(100.dp))
					LoadingAnimation(
						Modifier
							.align(Alignment.CenterHorizontally)
							.padding(32.dp)
							.size(80.dp)
					)
				}

				searchText.length >= 2 && predictions.isEmpty() && ! weatherVM.weatherState.isLoading -> {
					Text(
						"No results found",
						modifier = Modifier
							.padding(16.dp)
							.align(Alignment.CenterHorizontally),
						color = themeColors.textColor
					)
				}

				else -> {
					LazyColumn {
						itemsIndexed(predictions) { index, prediction ->
							PredictionItem(prediction, weatherVM) {
								weatherVM.fetchPlacesDetails(
									it.placeId
								) { latlng ->
									onLocationSelected(latlng)
								}
							}
							if(index < predictions.size - 1) Divider(
								color = themeColors.textColor.copy(alpha = 0.3f),
							)
						}
					}
				}
			}
		}
	}
}

@Composable
fun PredictionItem(
	prediction: AutocompletePrediction,
	weatherVM: WeatherVM,
	onClick: (AutocompletePrediction) -> Unit,
) {
	val themeColors = ThemeColors(
		textColor = weatherVM.weatherState.weatherInfo?.currentWeatherData?.weatherType?.textColor
			?: Color.White,
		bgColor = weatherVM.weatherState.weatherInfo?.currentWeatherData?.weatherType?.bgColor
			?: Color.Gray,
		darkBgColor = weatherVM.weatherState.weatherInfo?.currentWeatherData?.weatherType?.darkBgColor
			?: Color.DarkGray,
	)

	val primaryText = prediction.getPrimaryText(null).toString()
	val secondaryText = prediction.getSecondaryText(null).toString()

	Column(
		modifier = Modifier
			.fillMaxWidth()
			.clickable { onClick(prediction) }
			.padding(horizontal = 16.dp, vertical = 12.dp)
	) {
		Text(
			primaryText,
			color = themeColors.textColor,
			fontSize = 16.sp,
			fontWeight = FontWeight.Bold,
		)
		Text(
			secondaryText,
			fontSize = 12.sp,
			color = themeColors.textColor.copy(alpha = 0.8f),
		)
	}
}