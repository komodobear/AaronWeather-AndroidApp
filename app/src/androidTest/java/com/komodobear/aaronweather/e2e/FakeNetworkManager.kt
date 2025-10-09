package com.komodobear.aaronweather.e2e

import com.komodobear.aaronweather.data.NetworkManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class FakeNetworkManager : NetworkManager {
	private val _isNetworkAvailable = MutableStateFlow(true)
	override val isNetworkAvailable: StateFlow<Boolean> = _isNetworkAvailable

	override fun checkNetworkAvailability(){}

	override fun cleanup() {}

	fun setNetworkAvailable(available: Boolean) {
		_isNetworkAvailable.value = available
	}
}