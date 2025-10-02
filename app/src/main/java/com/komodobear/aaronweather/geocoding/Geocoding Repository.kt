package com.komodobear.aaronweather.geocoding

import android.util.Log
import com.komodobear.aaronweather.BuildConfig
import com.komodobear.aaronweather.location.LocationData
import javax.inject.Inject

interface GeocodingRepository {
	suspend fun fetchName(locationData: LocationData): String
}

class GeoCodingRepositoryImpl @Inject constructor(
	private val apiService: GeocodingApi
): GeocodingRepository {
	override suspend fun fetchName(locationData: LocationData): String {
		val latlng = "${locationData.latitude},${locationData.longitude}"
		return try {
			val response = apiService.getAddressFromCoordinates(latlng, BuildConfig.API_KEY)
			response.results
				.firstOrNull()
				?.address_components
				?.firstOrNull { "locality" in it.types }
				?.long_name ?: "Unknown"
		} catch (e: Exception) {
			Log.e("GeocodingRepository", "Error fetching city: ${e.message}", e)
			"Unknown"
		}
	}
}