package com.komodobear.aaronweather.e2e

import com.google.android.libraries.places.api.net.PlacesClient
import com.komodobear.aaronweather.di.AppModule
import com.komodobear.aaronweather.di.RepositoryModule
import com.komodobear.aaronweather.location.LocationUtilsInterface
import com.komodobear.aaronweather.weather.WeatherApi
import com.komodobear.aaronweather.weather.WeatherRepository
import com.komodobear.aaronweather.weather.WeatherRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockWebServer
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import javax.inject.Singleton

@Module
@TestInstallIn(
	components = [SingletonComponent::class],
	replaces = [AppModule::class]
)
object TestAppModule {

	@Provides
	@Singleton
	fun provideMockWebServer(): MockWebServer = MockWebServer()

	@Provides
	@Singleton
	fun provideOkHttpClient(): OkHttpClient = OkHttpClient
		.Builder()
		.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
		.build()

	@Provides
	@Singleton
	fun provideRetrofit(mockWebServer: MockWebServer, okHttpClient: OkHttpClient): Retrofit =
		Retrofit.Builder()
			.baseUrl(
				runBlocking(Dispatchers.IO) {
					mockWebServer.url("/").toString()
				}
			)
			.client(okHttpClient)
			.addConverterFactory(MoshiConverterFactory.create())
			.build()

	@Provides
	@Singleton
	fun provideWeatherApi(retrofit: Retrofit): WeatherApi =
		retrofit.create(WeatherApi::class.java)

	@Provides
	@Singleton
	fun provideClock(): Clock =
		Clock.fixed(
			Instant.parse("2025-01-01T00:00:00Z"),
			ZoneId.of("UTC")
		)

	@Provides
	@Singleton
	fun provideFakeLocationUtils(): FakeLocationUtils = FakeLocationUtils()

	@Provides
	@Singleton
	fun providePlacesClient(): PlacesClient {
		return mockk<PlacesClient>(relaxed = true)
	}

}

@Module
@TestInstallIn(
	components = [SingletonComponent::class],
	replaces = [RepositoryModule::class]
)abstract class TestRepositoryModule {
	@Binds
	@Singleton
	abstract fun bindWeatherRepository(
		weatherRepositoryImpl: WeatherRepositoryImpl
	): WeatherRepository

	@Binds
	@Singleton
	abstract fun bindLocationUtils(
		locationUtils: FakeLocationUtils
	): LocationUtilsInterface
}