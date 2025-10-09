package com.komodobear.aaronweather.usecases

import com.komodobear.aaronweather.model.LocationData
import com.komodobear.aaronweather.repository.DataStoreRepository
import javax.inject.Inject

class SaveLocationUseCase @Inject constructor(
	private val dataStoreRepository: DataStoreRepository
) {
	suspend operator fun invoke(location: LocationData) {
		dataStoreRepository.saveLocation(location)
	}
}