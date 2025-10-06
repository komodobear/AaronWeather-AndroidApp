package com.komodobear.aaronweather.api

import com.komodobear.aaronweather.geocoding.GeocodingResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingApi{
	@GET("maps/api/geocode/json")
	suspend fun getAddressFromCoordinates(
		@Query("latlng") latlng: String,
		@Query("key") apiKey: String
	): GeocodingResponse
}