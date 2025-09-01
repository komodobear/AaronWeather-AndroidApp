package com.example.aaronweather.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.aaronweather.R

@Composable
fun LoadingAnimation(
	modifier: Modifier,
) {
	val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading))

	val progress by animateLottieCompositionAsState(
		composition,
		iterations = LottieConstants.IterateForever,
		speed = 0.7f
	)

	LottieAnimation(composition = composition, progress = { progress }, modifier = modifier)
}