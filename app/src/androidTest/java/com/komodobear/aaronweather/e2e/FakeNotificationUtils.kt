package com.komodobear.aaronweather.e2e

import com.komodobear.aaronweather.data.NotificationUtils

class FakeNotificationUtils(): NotificationUtils {
	override fun hasNotificationPermission(): Boolean {
		return true
	}
}