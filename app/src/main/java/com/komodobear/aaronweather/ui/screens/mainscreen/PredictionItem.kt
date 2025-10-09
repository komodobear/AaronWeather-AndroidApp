package com.komodobear.aaronweather.ui.screens.mainscreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.komodobear.aaronweather.model.ThemeColors

@Composable
fun PredictionItem(
	prediction: AutocompletePrediction,
	themeColors: ThemeColors,
	onClick: (AutocompletePrediction) -> Unit,
) {

	val primaryText = prediction.getPrimaryText(null).toString()
	val secondaryText = prediction.getSecondaryText(null).toString()

	Column(
		modifier = Modifier
			.fillMaxWidth()
			.clickable { onClick(prediction) }
			.padding(horizontal = 16.dp, vertical = 12.dp)
	) {
		Text(
			primaryText,
			color = themeColors.textColor,
			fontSize = 16.sp,
			fontWeight = FontWeight.Bold,
		)
		Text(
			secondaryText,
			fontSize = 12.sp,
			color = themeColors.textColor.copy(alpha = 0.8f),
		)
	}
}