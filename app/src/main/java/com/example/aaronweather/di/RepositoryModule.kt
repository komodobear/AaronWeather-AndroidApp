package com.example.aaronweather.di

import com.example.aaronweather.weather.WeatherRepository
import com.example.aaronweather.weather.WeatherRepositoryImpl
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
}