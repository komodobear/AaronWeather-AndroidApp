package com.komodobear.aaronweather.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.komodobear.aaronweather.model.LocationData
import com.komodobear.aaronweather.repository.PlacesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlacesVM @Inject constructor(
	private val placesRepository: PlacesRepository
): ViewModel() {

	fun fetchPlacesDetails(
		placeId: String,
		onResult: (LocationData) -> Unit
	) {
		viewModelScope.launch(Dispatchers.IO) {
			placesRepository.fetchPlacesDetails(placeId, onResult)
		}
	}

	fun fetchPredictions(
		query: String,
		onResult: (List<AutocompletePrediction>) -> Unit
	) {
		viewModelScope.launch(Dispatchers.IO) {
			placesRepository.fetchPredictions(query, onResult)
		}
	}
}


