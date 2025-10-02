package com.komodobear.aaronweather

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

interface NetworkManagerInterface {
	val isNetworkAvailable: StateFlow<Boolean>
	fun checkNetworkAvailability()
	fun cleanup()
}

@Singleton
class NetworkManager @Inject constructor(
	@ApplicationContext private val context: Context
): NetworkManagerInterface {
	private val connectivityManager: ConnectivityManager by lazy {
		context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
	}

	private val _isNetworkAvailable = MutableStateFlow(false)
	override val isNetworkAvailable: StateFlow<Boolean> = _isNetworkAvailable.asStateFlow()

	private val networkCallback = object: ConnectivityManager.NetworkCallback() {
		override fun onAvailable(network: Network) {
			Log.d("NetworkManager", "onAvailable: Network available")
			_isNetworkAvailable.value = true
		}

		override fun onLost(network: Network) {
			Log.d("NetworkManager", "onLost: Network lost")
			_isNetworkAvailable.value = false
		}
	}

	override fun checkNetworkAvailability() {
		val network = connectivityManager.activeNetwork
		val capabilities = connectivityManager.getNetworkCapabilities(network)
		_isNetworkAvailable.value = capabilities != null &&
				capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
		Log.d("NetworkManager", "checkNetworkAvailability: Network available = ${_isNetworkAvailable.value}")
	}

	init {
		try {
			val request = NetworkRequest.Builder()
				.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
				.build()
			connectivityManager.registerNetworkCallback(request, networkCallback)
			checkNetworkAvailability()
			Log.d("NetworkManager", "init: Network callback registered")
		} catch(e: Exception) {
			Log.d("NetworkManager", "init: Network callback registration failed: ${e.message}")
			_isNetworkAvailable.value = false
		}
	}

	override fun cleanup() {
		connectivityManager.unregisterNetworkCallback(networkCallback)
	}
}