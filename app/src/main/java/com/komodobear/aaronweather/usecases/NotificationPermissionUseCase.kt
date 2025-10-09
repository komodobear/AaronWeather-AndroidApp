package com.komodobear.aaronweather.usecases

import com.komodobear.aaronweather.data.NotificationUtils
import javax.inject.Inject

class NotificationPermissionUseCase @Inject constructor(
	private val notificationUtils: NotificationUtils
){
	operator fun invoke(): Boolean {
		return notificationUtils.hasNotificationPermission()
	}
}