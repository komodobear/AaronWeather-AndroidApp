package com.komodobear.aaronweather.di

import android.app.Application
import com.komodobear.aaronweather.BuildConfig
import com.google.android.libraries.places.api.Places
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class WeatherApp: Application() {
	override fun onCreate() {
		super.onCreate()
		Places.initialize(applicationContext, BuildConfig.API_KEY)
	}
}