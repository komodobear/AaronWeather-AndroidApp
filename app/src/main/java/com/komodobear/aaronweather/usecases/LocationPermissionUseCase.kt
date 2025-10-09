package com.komodobear.aaronweather.usecases

import com.komodobear.aaronweather.data.LocationUtils
import javax.inject.Inject

class LocationPermissionUseCase @Inject constructor(
	private val locationUtils: LocationUtils
){
	operator fun invoke(): Boolean {
		return locationUtils.hasLocationPermission()
	}
}