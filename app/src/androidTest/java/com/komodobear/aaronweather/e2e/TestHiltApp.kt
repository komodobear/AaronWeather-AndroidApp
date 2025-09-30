package com.komodobear.aaronweather.e2e

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

class TestHiltApp : AndroidJUnitRunner() {
	override fun newApplication(cl: ClassLoader?, className: String?, context: Context?): Application {
		return super.newApplication(cl, "dagger.hilt.android.testing.HiltTestApplication", context)
	}
}