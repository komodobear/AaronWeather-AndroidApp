package com.komodobear.aaronweather.e2e

import android.content.Context
import com.komodobear.aaronweather.data.NotificationUtilsInterface

class FakeNotificationUtils(): NotificationUtilsInterface {
	override fun hasNotificationPermission(context: Context): Boolean {
		return true
	}
}