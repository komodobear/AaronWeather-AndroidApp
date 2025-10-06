package com.komodobear.aaronweather.di

import com.komodobear.aaronweather.data.LocationUtils
import com.komodobear.aaronweather.data.LocationUtilsInterface
import com.komodobear.aaronweather.data.NetworkManager
import com.komodobear.aaronweather.data.NetworkManagerInterface
import com.komodobear.aaronweather.repository.DataStoreRepository
import com.komodobear.aaronweather.repository.DataStoreRepositoryImpl
import com.komodobear.aaronweather.repository.GeoCodingRepositoryImpl
import com.komodobear.aaronweather.repository.GeocodingRepository
import com.komodobear.aaronweather.repository.WeatherRepository
import com.komodobear.aaronweather.repository.WeatherRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

	@Binds
	@Singleton
	abstract fun bindWeatherRepository(
		weatherRepositoryImpl: WeatherRepositoryImpl
	): WeatherRepository

	@Binds
	@Singleton
	abstract fun bindLocationUtils(
		locationUtils: LocationUtils
	): LocationUtilsInterface

	@Binds
	@Singleton
	abstract fun bindNetworkManager(
		networkManager: NetworkManager
	): NetworkManagerInterface

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
}