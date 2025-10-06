package com.komodobear.aaronweather.geocoding

data class GeocodingResponse(
	val results: List<GeocodingResult>
)

data class GeocodingResult(
	val address_components: List<AddressComponent>
)

data class AddressComponent(
	val long_name: String,
	val types: List<String>
)