package com.komodobear.aaronweather.data

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.komodobear.aaronweather.R
import com.komodobear.aaronweather.model.LocationData
import com.komodobear.aaronweather.repository.GeocodingRepository
import com.komodobear.aaronweather.repository.WeatherRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import com.komodobear.aaronweather.model.Result as DataResult

// alternative to notification worker
// currently not used

@AndroidEntryPoint
class NotificationService: Service() {

	@Inject
	lateinit var weatherRepository: WeatherRepository

	@Inject
	lateinit var geocodingRepository: GeocodingRepository

	@Inject
	lateinit var locationUtils: LocationUtilsInterface

	private val channelID = "weather_channel"
	private val notificationID = 1

	private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

	override fun onCreate() {
		super.onCreate()
		createNotificationChannel()
	}

	@RequiresApi(Build.VERSION_CODES.O)
	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

		val initialNotification = buildNotification("Fetching weather...","")
		startForeground(notificationID, initialNotification)

		serviceScope.launch {
			while(isActive){
				try{
					val location = locationUtils.getLocation()
					if(location!=null){
						val lat = location.latitude
						val lon = location.longitude
						when(val result = weatherRepository.getWeatherData(lat,lon)){
							is DataResult.Success ->{
								val current = result.data?.currentWeatherData
								val temp = current?.temperature ?: 0.0
								val hour = current?.time?.format(
									DateTimeFormatter.ofPattern("HH:mm"))
								val weather = current?.weatherType?.weatherDesc ?: "Unknown"
								val city = geocodingRepository.fetchName(LocationData(lat, lon))
								val title = "$city $hour"
								val text = "${"%.1f".format(temp)}°C, $weather "
								val notification = buildNotification(title, text)
								val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
								manager.notify(notificationID, notification)
							}
							is DataResult.Error -> {}
						}
					}
				}catch(e: Exception){
					Log.e("NotificationService", "onStartCommand: ${e.message}", e)
				}
				delay(15 * 60 * 1000L)
			}
		}
		return START_STICKY
	}

	override fun onDestroy() {
		serviceScope.cancel()
		super.onDestroy()
	}

	override fun onBind(intent: Intent?): IBinder? = null


	private fun buildNotification(title: String, text: String): Notification {
		return NotificationCompat.Builder(this, channelID)
			.setContentTitle(title)
			.setContentText(text)
			.setSmallIcon(R.drawable.ic_notification)
			.setOngoing(true)
			.build()
	}

	private fun createNotificationChannel() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val channel = NotificationChannel(
				channelID,
				"Weather Updates",
				NotificationManager.IMPORTANCE_DEFAULT
			).apply {
				description = "Channel for weather service updates"
			}
			val manager = getSystemService(NotificationManager::class.java)
			manager.createNotificationChannel(channel)
		}
	}
}