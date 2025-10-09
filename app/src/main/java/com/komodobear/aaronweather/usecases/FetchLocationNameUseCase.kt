package com.komodobear.aaronweather.usecases

import com.komodobear.aaronweather.model.LocationData
import com.komodobear.aaronweather.repository.GeocodingRepository
import javax.inject.Inject

class FetchLocationNameUseCase @Inject constructor(
	private val geocodingRepository: GeocodingRepository
) {
	suspend operator fun invoke(location: LocationData): String {
		val name = geocodingRepository.fetchName(location)
		return name
	}
}