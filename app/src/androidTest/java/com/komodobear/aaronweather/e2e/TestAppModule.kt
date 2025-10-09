package com.komodobear.aaronweather.e2e

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.google.android.libraries.places.api.net.PlacesClient
import com.komodobear.aaronweather.api.GeocodingApi
import com.komodobear.aaronweather.api.WeatherApi
import com.komodobear.aaronweather.data.LocationUtils
import com.komodobear.aaronweather.data.NetworkManager
import com.komodobear.aaronweather.data.NotificationUtils
import com.komodobear.aaronweather.di.AppModule
import com.komodobear.aaronweather.di.BindModule
import com.komodobear.aaronweather.model.dataStore
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
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockWebServer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
			.addConverterFactory(GsonConverterFactory.create())
			.build()

	@Provides
	@Singleton
	fun provideWeatherApi(retrofit: Retrofit): WeatherApi =
		retrofit.create(WeatherApi::class.java)

	@Provides
	@Singleton
	fun provideGeocodingApi(retrofit: Retrofit): GeocodingApi =
		retrofit.create(GeocodingApi::class.java)

	@Provides
	@Singleton
	fun provideClock(): Clock =
		Clock.fixed(
			Instant.parse("2025-01-01T00:00:00Z"),
			ZoneId.of("UTC")
		)

	@Provides
	@Singleton
	fun provideFakeNotificationUtils(): NotificationUtils = FakeNotificationUtils()

	@Provides
	@Singleton
	fun providePlacesClient(): PlacesClient {
		return mockk<PlacesClient>(relaxed = true)
	}

	@Provides
	@Singleton
	fun provideFakeNetworkManager(): NetworkManager = FakeNetworkManager()

	@Provides
	@Singleton
	fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
		context.dataStore
}

@Module
@TestInstallIn(
	components = [SingletonComponent::class],
	replaces = [BindModule::class]
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
	): LocationUtils

	@Binds
	@Singleton
	abstract fun bindGeocodingApi(
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

}