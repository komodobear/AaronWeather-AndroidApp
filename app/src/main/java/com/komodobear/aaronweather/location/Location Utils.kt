package com.komodobear.aaronweather.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.komodobear.aaronweather.WeatherVM
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

interface LocationUtilsInterface {
	fun requestLocationUpdates(weatherVM: WeatherVM)
	fun hasLocationPermission(context: Context): Boolean
}

class LocationUtils @Inject constructor(
	@ApplicationContext context: Context
): LocationUtilsInterface {

	private val _fusedLocationClient: FusedLocationProviderClient =
		LocationServices.getFusedLocationProviderClient(context)

	@SuppressLint("MissingPermission")
	override fun requestLocationUpdates(weatherVM: WeatherVM) {

		val locationCallback = object: LocationCallback() {

			override fun onLocationResult(locationResult: LocationResult) {

				super.onLocationResult(locationResult)
				locationResult.lastLocation?.let {

					val location = LocationData(latitude = it.latitude, longitude = it.longitude)
					Log.d("LocationUtils", "Location updated: $location")

					weatherVM.updateLocation(location)

					_fusedLocationClient.removeLocationUpdates(this)
				} ?: kotlin.run {
					Log.d("LocationUtils", "Location was null")
				}
			}
		}
		val locationRequest = LocationRequest.Builder(
			Priority.PRIORITY_HIGH_ACCURACY, 1000
		).build()

		_fusedLocationClient.requestLocationUpdates(
			locationRequest, locationCallback, Looper.getMainLooper()
		)
	}

	override fun hasLocationPermission(context: Context): Boolean {
		val fine = ContextCompat.checkSelfPermission(
			context,
			Manifest.permission.ACCESS_FINE_LOCATION
		) == PackageManager.PERMISSION_GRANTED

		val coarse = ContextCompat.checkSelfPermission(
			context,
			Manifest.permission.ACCESS_COARSE_LOCATION
		) == PackageManager.PERMISSION_GRANTED

		return fine || coarse
	}

}