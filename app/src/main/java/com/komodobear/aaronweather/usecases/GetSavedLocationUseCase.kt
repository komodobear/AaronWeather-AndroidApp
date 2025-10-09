package com.komodobear.aaronweather.usecases

import com.komodobear.aaronweather.model.LocationData
import com.komodobear.aaronweather.repository.DataStoreRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSavedLocationUseCase @Inject constructor(
	private val dataStoreRepository: DataStoreRepository
){
	operator fun invoke(): Flow<LocationData> {
		return dataStoreRepository.getLocation()
	}
}