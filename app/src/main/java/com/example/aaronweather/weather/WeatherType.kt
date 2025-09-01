package com.example.aaronweather.weather

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.example.aaronweather.R

sealed class WeatherType(
	val weatherDesc: String,
	@DrawableRes val iconRes: Int,
	val bgColor: Color,
	val darkBgColor: Color,
	val textColor: Color
) {
	object ClearSky: WeatherType(
		weatherDesc = "Clear sky",
		iconRes = R.drawable.ic_sunny,
		bgColor = Color(0xFFB3E5FC),
		darkBgColor = Color(0xFF81D4FA),
		textColor = Color(0xFF0D1C1D)
	)

	object MainlyClear: WeatherType(
		weatherDesc = "Mainly clear",
		iconRes = R.drawable.ic_sunnycloudy,
		bgColor = Color(0xFF81D4FA),
		darkBgColor = Color(0xFF4FC3F7),
		textColor = Color(0xFF071116)
	)

	object PartlyCloudy: WeatherType(
		weatherDesc = "Partly cloudy",
		iconRes = R.drawable.ic_sunnycloudy,
		bgColor = Color(0xFF64B5F6),
		darkBgColor = Color(0xFF42A5F5),
		textColor = Color(0xFF07121A)
	)

	object Overcast: WeatherType(
		weatherDesc = "Overcast",
		iconRes = R.drawable.ic_cloudy,
		bgColor = Color(0xFF90A4AE),
		darkBgColor = Color(0xFF788797),
		textColor = Color.White
	)

	object Foggy: WeatherType(
		weatherDesc = "Foggy",
		iconRes = R.drawable.ic_very_cloudy,
		bgColor = Color(0xFFB0BEC5),
		darkBgColor = Color(0xFF78909C),
		textColor = Color.White
	)

	object DepositingRimeFog: WeatherType(
		weatherDesc = "Depositing rime fog",
		iconRes = R.drawable.ic_very_cloudy,
		bgColor = Color(0xFFCFD8DC),
		darkBgColor = Color(0xFF90A4AE),
		textColor = Color.White
	)

	object LightDrizzle: WeatherType(
		weatherDesc = "Light drizzle",
		iconRes = R.drawable.ic_rainshower,
		bgColor = Color(0xFF64B5F6),
		darkBgColor = Color(0xFF42A5F5),
		textColor = Color.White
	)

	object ModerateDrizzle: WeatherType(
		weatherDesc = "Moderate drizzle",
		iconRes = R.drawable.ic_rainshower,
		bgColor = Color(0xFF42A5F5),
		darkBgColor = Color(0xFF2196F3),
		textColor = Color.White
	)

	object DenseDrizzle: WeatherType(
		weatherDesc = "Dense drizzle",
		iconRes = R.drawable.ic_rainshower,
		bgColor = Color(0xFF1976D2),
		darkBgColor = Color(0xFF0D47A1),
		textColor = Color.White
	)

	object LightFreezingDrizzle: WeatherType(
		weatherDesc = "Slight freezing drizzle",
		iconRes = R.drawable.ic_snowyrainy,
		bgColor = Color(0xFF90CAF9),
		darkBgColor = Color(0xFF64B5F6),
		textColor = Color.Black
	)

	object DenseFreezingDrizzle: WeatherType(
		weatherDesc = "Dense freezing drizzle",
		iconRes = R.drawable.ic_snowyrainy,
		bgColor = Color(0xFF42A5F5),
		darkBgColor = Color(0xFF2196F3),
		textColor = Color.White
	)

	object SlightRain: WeatherType(
		weatherDesc = "Slight rain",
		iconRes = R.drawable.ic_rainy,
		bgColor = Color(0xFF2196F3),
		darkBgColor = Color(0xFF1E88E5),
		textColor = Color.White
	)

	object ModerateRain: WeatherType(
		weatherDesc = "Rainy",
		iconRes = R.drawable.ic_rainy,
		bgColor = Color(0xFF1E88E5),
		darkBgColor = Color(0xFF1565C0),
		textColor = Color.White
	)

	object HeavyRain: WeatherType(
		weatherDesc = "Heavy rain",
		iconRes = R.drawable.ic_rainy,
		bgColor = Color(0xFF1565C0),
		darkBgColor = Color(0xFF0D47A1),
		textColor = Color.White
	)

	object HeavyFreezingRain: WeatherType(
		weatherDesc = "Heavy freezing rain",
		iconRes = R.drawable.ic_snowyrainy,
		bgColor = Color(0xFF003C8F),
		darkBgColor = Color(0xFF002171),
		textColor = Color.White
	)

	object SlightSnowFall: WeatherType(
		weatherDesc = "Slight snow fall",
		iconRes = R.drawable.ic_snowy,
		bgColor = Color(0xFFE3F2FD),
		darkBgColor = Color(0xFFBBDEFB),
		textColor = Color.Black
	)

	object ModerateSnowFall: WeatherType(
		weatherDesc = "Moderate snow fall",
		iconRes = R.drawable.ic_heavysnow,
		bgColor = Color(0xFFCFD8DC),
		darkBgColor = Color(0xFFB0BEC5),
		textColor = Color.Black
	)

	object HeavySnowFall: WeatherType(
		weatherDesc = "Heavy snow fall",
		iconRes = R.drawable.ic_heavysnow,
		bgColor = Color(0xFF90A4AE),
		darkBgColor = Color(0xFF78909C),
		textColor = Color.White
	)

	object SnowGrains: WeatherType(
		weatherDesc = "Snow grains",
		iconRes = R.drawable.ic_heavysnow,
		bgColor = Color(0xFF90CAF9),
		darkBgColor = Color(0xFF64B5F6),
		textColor = Color(0xFF060B0F)
	)

	object SlightRainShowers: WeatherType(
		weatherDesc = "Slight rain showers",
		iconRes = R.drawable.ic_rainshower,
		bgColor = Color(0xFF64B5F6),
		darkBgColor = Color(0xFF42A5F5),
		textColor = Color.White
	)

	object ModerateRainShowers: WeatherType(
		weatherDesc = "Moderate rain showers",
		iconRes = R.drawable.ic_rainshower,
		bgColor = Color(0xFF42A5F5),
		darkBgColor = Color(0xFF2196F3),
		textColor = Color.White
	)

	object ViolentRainShowers: WeatherType(
		weatherDesc = "Violent rain showers",
		iconRes = R.drawable.ic_rainshower,
		bgColor = Color(0xFF1976D2),
		darkBgColor = Color(0xFF0D47A1),
		textColor = Color.White
	)

	object SlightSnowShowers: WeatherType(
		weatherDesc = "Light snow showers",
		iconRes = R.drawable.ic_snowy,
		bgColor = Color(0xFFE3F2FD),
		darkBgColor = Color(0xFFBBDEFB),
		textColor = Color.Black
	)

	object HeavySnowShowers: WeatherType(
		weatherDesc = "Heavy snow showers",
		iconRes = R.drawable.ic_snowy,
		bgColor = Color(0xFF90A4AE),
		darkBgColor = Color(0xFF78909C),
		textColor = Color.White
	)

	object ModerateThunderstorm: WeatherType(
		weatherDesc = "Moderate thunderstorm",
		iconRes = R.drawable.ic_thunder,
		bgColor = Color(0xFF546E7A),
		darkBgColor = Color(0xFF37474F),
		textColor = Color.White
	)

	object SlightHailThunderstorm: WeatherType(
		weatherDesc = "Thunderstorm with slight hail",
		iconRes = R.drawable.ic_rainythunder,
		bgColor = Color(0xFF455A64),
		darkBgColor = Color(0xFF263238),
		textColor = Color.White
	)

	object HeavyHailThunderstorm: WeatherType(
		weatherDesc = "Thunderstorm with heavy hail",
		iconRes = R.drawable.ic_rainythunder,
		bgColor = Color(0xFF102027),
		darkBgColor = Color(0xFF000A12),
		textColor = Color.White
	)


	companion object {
		fun fromWMO(code: Int): WeatherType {
			return when(code) {
				0 -> ClearSky
				1 -> MainlyClear
				2 -> PartlyCloudy
				3 -> Overcast
				45 -> Foggy
				48 -> DepositingRimeFog
				51 -> LightDrizzle
				53 -> ModerateDrizzle
				55 -> DenseDrizzle
				56 -> LightFreezingDrizzle
				57 -> DenseFreezingDrizzle
				61 -> SlightRain
				63 -> ModerateRain
				65 -> HeavyRain
				66 -> LightFreezingDrizzle
				67 -> HeavyFreezingRain
				71 -> SlightSnowFall
				73 -> ModerateSnowFall
				75 -> HeavySnowFall
				77 -> SnowGrains
				80 -> SlightRainShowers
				81 -> ModerateRainShowers
				82 -> ViolentRainShowers
				85 -> SlightSnowShowers
				86 -> HeavySnowShowers
				95 -> ModerateThunderstorm
				96 -> SlightHailThunderstorm
				99 -> HeavyHailThunderstorm
				else -> ClearSky
			}
		}
	}
}