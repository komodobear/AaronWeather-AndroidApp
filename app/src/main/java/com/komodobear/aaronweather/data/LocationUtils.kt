package com.komodobear.aaronweather.data

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
import com.komodobear.aaronweather.model.LocationData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import kotlin.coroutines.resume

interface LocationUtils {
	fun hasLocationPermission(): Boolean
	suspend fun getLocation(): LocationData?
}

class LocationUtilsImpl @Inject constructor(
	@ApplicationContext private val context: Context
): LocationUtils {

	private val _fusedLocationClient: FusedLocationProviderClient =
		LocationServices.getFusedLocationProviderClient(context)

	override fun hasLocationPermission(): Boolean {
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

	@SuppressLint("MissingPermission")
	override suspend fun getLocation(): LocationData? {
		return withTimeoutOrNull(10_000L) {

			suspendCancellableCoroutine { cont ->

				val locationRequest = LocationRequest.Builder(
					Priority.PRIORITY_HIGH_ACCURACY, 1_000L
				).setMinUpdateIntervalMillis(500L).build()

				val callback = object: LocationCallback() {
					override fun onLocationResult(result: LocationResult) {
						super.onLocationResult(result)
						result.lastLocation?.let { loc ->
							val location = LocationData(loc.latitude, loc.longitude)
							if(cont.isActive) cont.resume(location)
						} ?: run {
							if(cont.isActive) cont.resume(null)
						}
						try {
							_fusedLocationClient.removeLocationUpdates(this)
						} catch(e: Exception) {
							Log.e("LocationUtilsImpl", "getLocationOnce exception: ${e.message}", e)
						}
					}
				}
				_fusedLocationClient.requestLocationUpdates(
					locationRequest, callback, Looper.getMainLooper()
				)
				cont.invokeOnCancellation {
					try{
						_fusedLocationClient.removeLocationUpdates(callback)
					}catch(e: Exception){
						Log.e("LocationUtilsImpl", "getLocationOnce cancellation exception: ${e.message}", e)
					}
				}
			}
		}
	}
}