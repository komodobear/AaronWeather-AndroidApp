package com.komodobear.aaronweather.repository

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.komodobear.aaronweather.model.LocationData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

object LocationKeys {
	val LAT_KEY = doublePreferencesKey("latitude")
	val LON_KEY = doublePreferencesKey("longitude")
}

interface DataStoreRepository {
	suspend fun saveLocation(locationData: LocationData)
	fun getLocation(): Flow<LocationData>
}

class DataStoreRepositoryImpl @Inject constructor(
	private val datastore: DataStore<Preferences>
): DataStoreRepository {

	override suspend fun saveLocation(locationData: LocationData) {
		datastore.edit { preferences ->
			preferences[LocationKeys.LAT_KEY] = locationData.latitude
			preferences[LocationKeys.LON_KEY] = locationData.longitude
		}
		Log.d(
			"DataStoreRepository",
			"saveLocation: Location: lat = ${locationData.latitude}, lon = ${locationData.longitude}"
		)
	}

	override fun getLocation(): Flow<LocationData> = datastore.data
		.catch { exception ->
			if(exception is IOException) emit(emptyPreferences()) else throw exception
		}
		.map { preferences ->
			val lat = preferences[LocationKeys.LAT_KEY] ?: 0.0
			val lon = preferences[LocationKeys.LON_KEY] ?: 0.0
			LocationData(lat, lon)
		}
}

