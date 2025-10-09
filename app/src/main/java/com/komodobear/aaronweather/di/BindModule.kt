package com.komodobear.aaronweather.di

import com.komodobear.aaronweather.data.LocationUtils
import com.komodobear.aaronweather.data.LocationUtilsImpl
import com.komodobear.aaronweather.data.NetworkManager
import com.komodobear.aaronweather.data.NetworkManagerImpl
import com.komodobear.aaronweather.data.NotificationUtils
import com.komodobear.aaronweather.data.NotificationUtilsImpl
import com.komodobear.aaronweather.repository.DataStoreRepository
import com.komodobear.aaronweather.repository.DataStoreRepositoryImpl
import com.komodobear.aaronweather.repository.GeoCodingRepositoryImpl
import com.komodobear.aaronweather.repository.GeocodingRepository
import com.komodobear.aaronweather.repository.PlacesRepository
import com.komodobear.aaronweather.repository.PlacesRepositoryImpl
import com.komodobear.aaronweather.repository.WeatherRepository
import com.komodobear.aaronweather.repository.WeatherRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BindModule {

	@Binds
	@Singleton
	abstract fun bindWeatherRepository(
		weatherRepository: WeatherRepositoryImpl
	): WeatherRepository

	@Binds
	@Singleton
	abstract fun bindLocationUtils(
		locationUtils: LocationUtilsImpl
	): LocationUtils

	@Binds
	@Singleton
	abstract fun bindNetworkManager(
		networkManager: NetworkManagerImpl
	): NetworkManager

	@Binds
	@Singleton
	abstract fun bindGeocodingRepository(
		geocodingRepository: GeoCodingRepositoryImpl
	): GeocodingRepository

	@Binds
	@Singleton
	abstract fun bindDataStoreRepository(
		datastoreRepository: DataStoreRepositoryImpl
	): DataStoreRepository

	@Binds
	@Singleton
	abstract fun bindPlacesRepository(
		placesRepository: PlacesRepositoryImpl
	): PlacesRepository

	@Binds
	@Singleton
	abstract fun bindNotificationUtils(
		notificationRepository: NotificationUtilsImpl
	): NotificationUtils
}