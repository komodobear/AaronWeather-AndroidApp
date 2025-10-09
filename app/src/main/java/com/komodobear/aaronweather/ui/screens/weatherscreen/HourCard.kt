package com.komodobear.aaronweather.ui.screens.weatherscreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.komodobear.aaronweather.model.ThemeColors
import com.komodobear.aaronweather.model.weatherdata.WeatherState
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HourCard(
	state: WeatherState,
	hour: Int,
	modifier: Modifier = Modifier
) {
	val themeColors = ThemeColors(
		textColor = state.weatherInfo?.currentWeatherData?.weatherType?.textColor ?: Color.White,
		bgColor = state.weatherInfo?.currentWeatherData?.weatherType?.bgColor ?: Color.Gray,
		darkBgColor = state.weatherInfo?.currentWeatherData?.weatherType?.darkBgColor
			?: Color.DarkGray,
	)

	state.weatherInfo?.weatherDataPerDay?.get(0)?.let { data ->

		val listState = rememberLazyListState()

		LaunchedEffect(hour) {
			val rowIndex = data.indexOfFirst { it.time.hour >= hour }
			if(rowIndex >= 0) {
				listState.scrollToItem(rowIndex)
			}
		}

		Card(
			modifier = modifier,
			shape = RoundedCornerShape(16.dp),
			colors = CardDefaults.cardColors(themeColors.darkBgColor.copy(alpha = 0.7f))
		) {
			Column(Modifier.padding(16.dp)) {

				val currentDate = data.firstOrNull()?.time?.format(
					DateTimeFormatter.ofPattern("MMMM, d", Locale.ENGLISH)
				) ?: ""

				Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
					Text("Today", fontSize = 16.sp, color = themeColors.textColor)
					Text(currentDate, fontSize = 16.sp, color = themeColors.textColor)
				}

				Spacer(modifier = Modifier.height(6.dp))

				Divider()

				Spacer(modifier = Modifier.height(12.dp))

				LazyRow(
					state = listState,
					horizontalArrangement = Arrangement.SpaceBetween,
					modifier = Modifier.fillMaxWidth()
				) {
					items(data) { weatherData ->
						HourItem(weatherData, state)
					}
				}
			}
		}

	}
}