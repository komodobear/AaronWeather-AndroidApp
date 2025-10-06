package com.komodobear.aaronweather.e2e

import android.content.Context
import com.komodobear.aaronweather.data.LocationUtilsInterface
import com.komodobear.aaronweather.model.LocationData
import javax.inject.Inject

class FakeLocationUtils @Inject constructor() : LocationUtilsInterface {

	private val fakeLocation: LocationData = LocationData(
		latitude = 54.4966122,
		longitude = 18.5393955
	)

	override fun hasLocationPermission(context: Context): Boolean {
		return true
	}

	override suspend fun getLocation(): LocationData? {
		return LocationData(fakeLocation.latitude, fakeLocation.longitude)
	}

}