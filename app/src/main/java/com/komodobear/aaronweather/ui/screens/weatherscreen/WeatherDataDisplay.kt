package com.komodobear.aaronweather.ui.screens.weatherscreen

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.komodobear.aaronweather.model.ThemeColors
import com.komodobear.aaronweather.model.weatherdata.WeatherState

@Composable
fun WeatherDataDisplay(
	value: Int,
	unit: String,
	icon: ImageVector,
	modifier: Modifier = Modifier,
	textStyle: TextStyle = TextStyle(),
	state: WeatherState
) {
	val themeColors = ThemeColors(
		textColor = state.weatherInfo?.currentWeatherData?.weatherType?.textColor ?: Color.White,
		bgColor = state.weatherInfo?.currentWeatherData?.weatherType?.bgColor ?: Color.Gray,
		darkBgColor = state.weatherInfo?.currentWeatherData?.weatherType?.darkBgColor
			?: Color.DarkGray,
	)

	Row(
		verticalAlignment = Alignment.CenterVertically,
		modifier = modifier
	) {
		Icon(
			imageVector = icon,
			contentDescription = null,
			tint = themeColors.textColor,
			modifier = Modifier.size(26.dp)
		)
		Spacer(modifier = Modifier.width(4.dp))
		Text(
			text = "$value$unit",
			fontSize = 18.sp,
			style = textStyle,
			color = themeColors.textColor
		)
	}
}