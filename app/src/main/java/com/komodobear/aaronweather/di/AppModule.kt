package com.komodobear.aaronweather.di

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.komodobear.aaronweather.BuildConfig
import com.komodobear.aaronweather.api.GeocodingApi
import com.komodobear.aaronweather.api.WeatherApi
import com.komodobear.aaronweather.data.NotificationUtils
import com.komodobear.aaronweather.data.NotificationUtilsInterface
import com.komodobear.aaronweather.model.dataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.time.Clock
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)

object AppModule {
	@Provides
	@Singleton
	fun provideWeatherApi(): WeatherApi {
		return Retrofit.Builder()
			.baseUrl("https://api.open-meteo.com/")
			.addConverterFactory(MoshiConverterFactory.create())
			.build()
			.create(WeatherApi::class.java)
	}

	@Provides
	@Singleton
	fun provideGeocodingApi(): GeocodingApi {
		return Retrofit.Builder()
			.baseUrl("https://maps.googleapis.com/")
			.addConverterFactory(GsonConverterFactory.create())
			.build()
			.create(GeocodingApi::class.java)
	}

	@RequiresApi(Build.VERSION_CODES.O)
	@Provides
	@Singleton
	fun provideClock(): Clock {
		return Clock.systemDefaultZone()
	}

	@Provides
	@Singleton
	fun providePlacesClient(app: Application): PlacesClient {
		if(! Places.isInitialized()) {
			Places.initialize(app, BuildConfig.API_KEY)
		}
		return Places.createClient(app)
	}

	@Provides
	@Singleton
	fun provideNotificationUtils(): NotificationUtilsInterface {
		return NotificationUtils()
	}

	@Provides
	@Singleton
	fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
		context.dataStore
}