package com.komodobear.aaronweather.di

import com.komodobear.aaronweather.location.LocationUtils
import com.komodobear.aaronweather.location.LocationUtilsInterface
import com.komodobear.aaronweather.weather.WeatherRepository
import com.komodobear.aaronweather.weather.WeatherRepositoryImpl
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
}