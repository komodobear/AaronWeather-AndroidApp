package com.komodobear.aaronweather.di

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.komodobear.aaronweather.data.NotificationWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class WeatherApp: Application(), Configuration.Provider {

	@Inject
	lateinit var workerFactory: HiltWorkerFactory

	override val workManagerConfiguration: Configuration
		get() = Configuration.Builder()
			.setWorkerFactory(workerFactory)
			.build()

	override fun onCreate() {
		super.onCreate()
		schedulePeriodicWorkerIfNeeded()
		createNotificationChannel()
	}

	private fun schedulePeriodicWorkerIfNeeded() {
		val periodic = PeriodicWorkRequestBuilder<NotificationWorker>(15, TimeUnit.MINUTES)
			.addTag("notification_worker")
			.build()

		WorkManager.getInstance(this).enqueueUniquePeriodicWork(
			"notification_worker",
			ExistingPeriodicWorkPolicy.KEEP,
			periodic
		)
	}

	private fun createNotificationChannel() {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val name = "Weather updates"
			val descriptionText = "Weather info channel"
			val importance = NotificationManager.IMPORTANCE_DEFAULT
			val channel = NotificationChannel("weather_channel", name, importance).apply {
				description = descriptionText
			}
			val manager: NotificationManager =
				applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
			manager.createNotificationChannel(channel)
		}
	}
}