package com.komodobear.aaronweather.usecases

import com.komodobear.aaronweather.data.LocationUtils
import com.komodobear.aaronweather.model.LocationData
import javax.inject.Inject

class GetLocationUseCase @Inject constructor(
	private val locationUtils: LocationUtils
){
	suspend operator fun invoke(): LocationData? {
		return locationUtils.getLocation()
	}
}