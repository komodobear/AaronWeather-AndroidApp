package com.komodobear.aaronweather.e2e

import android.content.Context
import com.komodobear.aaronweather.WeatherVM
import com.komodobear.aaronweather.location.LocationData
import com.komodobear.aaronweather.location.LocationUtilsInterface

class FakeLocationUtils(
	private val fakeLocation: LocationData = LocationData(
		latitude = 54.4966122,
		longitude = 18.5393955
	)
) : LocationUtilsInterface {

	override fun requestLocationUpdates(weatherVM: WeatherVM) {
		weatherVM.updateLocation(fakeLocation)
	}

	override fun hasLocationPermission(context: Context): Boolean {
		return true
	}
}