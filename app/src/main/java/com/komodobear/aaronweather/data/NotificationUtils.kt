package com.komodobear.aaronweather.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

interface NotificationUtils {
    fun hasNotificationPermission(): Boolean
}

class NotificationUtilsImpl @Inject constructor(
	@ApplicationContext private val context: Context
): NotificationUtils{
	override fun hasNotificationPermission(): Boolean {
		return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
			ContextCompat.checkSelfPermission(
				context,
				Manifest.permission.POST_NOTIFICATIONS
			) == PackageManager.PERMISSION_GRANTED
		} else {
			true
		}
	}
}