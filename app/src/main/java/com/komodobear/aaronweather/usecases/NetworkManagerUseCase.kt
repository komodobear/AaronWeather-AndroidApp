package com.komodobear.aaronweather.usecases

import com.komodobear.aaronweather.data.NetworkManager
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class NetworkManagerUseCase @Inject constructor(
	private val networkManager: NetworkManager
) {
	val isNetworkAvailable: StateFlow<Boolean>
		get() = networkManager.isNetworkAvailable

	fun start() {
		networkManager.checkNetworkAvailability()
	}

	fun stop() {
		networkManager.cleanup()
	}
}