package com.komodobear.aaronweather.e2e

import com.komodobear.aaronweather.data.LocationUtils
import com.komodobear.aaronweather.model.LocationData
import javax.inject.Inject

class FakeLocationUtils @Inject constructor() : LocationUtils {

	private val fakeLocation: LocationData = LocationData(
		latitude = 54.4966122,
		longitude = 18.5393955
	)

	override fun hasLocationPermission(): Boolean {
		return true
	}

	override suspend fun getLocation(): LocationData? {
		return LocationData(fakeLocation.latitude, fakeLocation.longitude)
	}

}