package com.komodobear.aaronweather.data

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.komodobear.aaronweather.MainActivity
import com.komodobear.aaronweather.R
import com.komodobear.aaronweather.model.LocationData
import com.komodobear.aaronweather.usecases.UseCasesHolder
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.time.format.DateTimeFormatter
import com.komodobear.aaronweather.model.Result as DataResult

@HiltWorker
class NotificationWorker @AssistedInject constructor(
	@Assisted appContext: Context,
	@Assisted params: WorkerParameters,
	private val useCases: UseCasesHolder
): CoroutineWorker(appContext, params) {

	private val tag = "NotificationWorker"

	@RequiresApi(Build.VERSION_CODES.O)
	override suspend fun doWork(): Result {

		Log.i(tag, "doWork: Running notification worker")

		if(! useCases.hasNotificationPermission()) {
			Log.w(tag, "Notification permission not granted - retry")
			return Result.retry()
		}

		val savedLocation = try {
			useCases.getSavedLocation().first()
		} catch(e: Exception) {
			Log.e(tag, "Failed to read location from DataStore", e)
			null
		}

		val location = if(savedLocation == null ||
			(savedLocation.latitude == 0.0 && savedLocation.longitude == 0.0)
		) {
			val userLocation = useCases.getLocation()
			if(userLocation == null) {
				Log.w(
					tag,
					"No location available (DataStore empty and locationUtils returned null) - stop"
				)
				return Result.success()
			} else userLocation
		} else {
			LocationData(savedLocation.latitude, savedLocation.longitude)
		}

		when(val result = useCases.getWeather(location)) {
			is DataResult.Success -> {
				val current = result.data?.currentWeatherData
				val weather = current?.weatherType?.weatherDesc ?: "Unknown"
				val temp = current?.temperature ?: 0.0
				val hour = current?.time?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: ""
				val city = useCases.fetchLocationName(
					LocationData(
						location.latitude,
						location.longitude
					)
				)

				val title = "$city $hour"
				val text = "${"%.1f".format(temp)}°C, $weather "

				val intent = Intent(applicationContext, MainActivity::class.java).apply {
					flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
				}

				val pendingIntent = PendingIntent.getActivity(
					applicationContext,
					0,
					intent,
					PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
				)

				val notification = NotificationCompat.Builder(applicationContext, "weather_channel")
					.setContentTitle(title)
					.setContentText(text)
					.setSmallIcon(R.drawable.ic_notification)
					.setContentIntent(pendingIntent)
					.setAutoCancel(true)
					.setOngoing(false)
					.build()

				try {
					NotificationManagerCompat.from(applicationContext)
						.notify(1, notification)
					Log.i(tag, "Notification showed")
				} catch(se: SecurityException) {
					Log.e(tag, "Failed to display notification", se)
				} catch(e: Exception) {
					Log.e(tag, "Failed to display notification", e)
				}
			}

			is DataResult.Error -> {}
		}

		return Result.success()
	}
}
