package com.komodobear.aaronweather.ui.screens.mainscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.komodobear.aaronweather.model.LocationData
import com.komodobear.aaronweather.model.ThemeColors
import com.komodobear.aaronweather.model.weatherdata.WeatherState
import com.komodobear.aaronweather.ui.composables.LoadingAnimation
import com.komodobear.aaronweather.viewmodels.PlacesVM

@Composable
fun LocationSearchDrawer(
	onLocationSelected: (LocationData) -> Unit,
	themeColors: ThemeColors,
	state: WeatherState,
	placesVM: PlacesVM = hiltViewModel()
) {

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
							placesVM.fetchPredictions(text) { results ->
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

				state.isLoading && searchText.length <= 2 -> {
					Spacer(modifier = Modifier.size(100.dp))
					LoadingAnimation(
						Modifier
							.align(Alignment.CenterHorizontally)
							.padding(32.dp)
							.size(80.dp)
					)
				}

				searchText.length >= 2 && predictions.isEmpty() && ! state.isLoading -> {
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
							PredictionItem(prediction, themeColors) {
								placesVM.fetchPlacesDetails(
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