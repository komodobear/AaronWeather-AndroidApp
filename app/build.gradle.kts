import java.util.Properties

plugins {
	alias(libs.plugins.android.application)
	alias(libs.plugins.kotlin.android)
	alias(libs.plugins.kotlin.compose)
	alias(libs.plugins.hilt)
	kotlin("kapt")
}

android {
	namespace = "com.komodobear.aaronweather"
	compileSdk = 35

	val apiKeyFromLocal: String? = run {
		val localProperties = rootProject.file("local.properties")
		if (localProperties.exists()) {
			val props = Properties().apply { load(localProperties.inputStream()) }
			(props["API_KEY"] as? String)?.takeIf { it.isNotBlank() }
		} else null
	}

	val apiKeyFromProject: String? = (project.findProperty("API_KEY") as? String)?.takeIf { it.isNotBlank() }
	val apiKeyFromEnv: String? = System.getenv("API_KEY")?.takeIf { it.isNotBlank() }

	val apiKey: String = apiKeyFromLocal ?: apiKeyFromProject ?: apiKeyFromEnv ?: ""

	defaultConfig {
		applicationId = "com.komodobear.aaronweather"
		minSdk = 24
		targetSdk = 35
		versionCode = 1
		versionName = "1.0"

		testInstrumentationRunner = "com.komodobear.aaronweather.e2e.TestHiltApp"

		val localProperties = project.rootProject.file("local.properties")
		if(localProperties.exists()) {
			val properties = Properties().apply {
				load(localProperties.inputStream())
			}
			buildConfigField("String", "API_KEY", "\"$apiKey\"")
		}
	}

	testOptions {
		animationsDisabled = true
	}


	buildTypes {
		release {
			isMinifyEnabled = false
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"proguard-rules.pro"
			)
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_11
		targetCompatibility = JavaVersion.VERSION_11
	}
	kotlinOptions {
		jvmTarget = "11"
	}
	buildFeatures {
		compose = true
		buildConfig = true
	}
	packaging {
		resources {
			excludes += setOf(
				"META-INF/LICENSE.md",
				"META-INF/LICENSE-notice.md"
			)
		}
	}
}

dependencies {
//  data store
	implementation(libs.androidx.datastore.preferences)

//  work manager
	implementation(libs.androidx.work.runtime.ktx)

//  places api
	implementation(libs.places)
	implementation(libs.maps.compose)

//  hilt
	implementation(libs.hilt.android)
	implementation(libs.androidx.junit.ktx)
	implementation(libs.androidx.hilt.work)
	implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)
	implementation(libs.androidx.hilt.navigation.compose)
	kapt(libs.androidx.hilt.compiler)
	kapt(libs.hilt.android.compiler)

//  retrofit
	implementation(libs.retrofit)
	implementation(libs.converter.moshi)
	implementation(libs.converter.gson)
	implementation(libs.logging.interceptor)

//  location
	implementation(libs.play.services.location)

//  UI
	implementation(platform(libs.androidx.compose.bom))
	implementation(libs.ui)
	implementation(libs.androidx.ui.graphics)
	implementation(libs.androidx.ui.tooling.preview)
	implementation(libs.androidx.activity.compose)
	implementation(libs.androidx.material)
	implementation(libs.androidx.material3)
	implementation(libs.lottie.compose)
	implementation(libs.androidx.navigation.compose)

//  core / lifecycle
	implementation(libs.androidx.core.ktx)
	implementation(libs.androidx.rules)
	implementation(libs.androidx.lifecycle.runtime.ktx)
	implementation(libs.androidx.lifecycle.viewmodel.compose)

//  test
	testImplementation(libs.junit)
	testImplementation(libs.androidx.core.testing)
	testImplementation(libs.mockk)
	testImplementation(libs.kotlinx.coroutines.test)
	testImplementation(kotlin("test"))
	androidTestImplementation(libs.androidx.junit.v120)
	androidTestImplementation(libs.androidx.espresso.core)
	androidTestImplementation(platform(libs.androidx.compose.bom))
	androidTestImplementation(libs.androidx.ui.test.junit4)
	androidTestImplementation(libs.mockk.android)
	androidTestImplementation(libs.hilt.android.testing)
	androidTestImplementation(libs.mockwebserver)
	debugImplementation(libs.androidx.ui.tooling)
	debugImplementation(libs.androidx.ui.test.manifest)
	debugImplementation(libs.ui.test.manifest)
	kaptAndroidTest(libs.hilt.compiler)
}