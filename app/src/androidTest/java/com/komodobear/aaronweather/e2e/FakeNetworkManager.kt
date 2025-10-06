package com.komodobear.aaronweather.e2e

import com.komodobear.aaronweather.data.NetworkManagerInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class FakeNetworkManager : NetworkManagerInterface {
	private val _isNetworkAvailable = MutableStateFlow(true)
	override val isNetworkAvailable: StateFlow<Boolean> = _isNetworkAvailable

	override fun checkNetworkAvailability(){}

	override fun cleanup() {}

	fun setNetworkAvailable(available: Boolean) {
		_isNetworkAvailable.value = available
	}
}