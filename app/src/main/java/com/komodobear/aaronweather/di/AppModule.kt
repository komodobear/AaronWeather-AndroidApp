package com.komodobear.aaronweather.di

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.komodobear.aaronweather.BuildConfig
import com.komodobear.aaronweather.weather.WeatherApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
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

    @RequiresApi(Build.VERSION_CODES.O)
	@Provides
    @Singleton
    fun provideClock(): Clock {
        return Clock.systemDefaultZone()
    }

    @Provides
    @Singleton
    fun providePlacesClient(app: Application): PlacesClient {
        if (!Places.isInitialized()) {
            Places.initialize(app, BuildConfig.API_KEY)
        }
        return Places.createClient(app)
    }
}