import java.util.Properties

// 1. Load local.properties
val localProps = Properties().apply {
    rootProject.file("local.properties").takeIf { it.exists() }?.inputStream()?.use { load(it) }
}

// 2. Read your key (or empty string if missing)
val catApiKey: String = localProps.getProperty("CAT_API_KEY", "")

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    id("kotlinx-serialization")
}

android {
    namespace = "com.example.catapult"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.catapult"
        minSdk = 34
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        buildFeatures {
            buildConfig = true     // ← assignment syntax in Kotlin-DSL
            compose     = true
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "CAT_API_KEY", "\"$catApiKey\"")
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
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation ("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-compiler:2.48")

    implementation("androidx.compose.material:material-icons-extended:1.4.3")

    // Navigation Compose
    implementation ("androidx.navigation:navigation-compose:2.7.2")
    implementation ("androidx.hilt:hilt-navigation-compose:1.0.0")

    // Retrofit + Kotlinx Serialization
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // Coroutines
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation ("androidx.hilt:hilt-navigation-compose:1.0.0")
    implementation ("io.coil-kt:coil-compose:2.4.0")

    // Room
    implementation ("androidx.room:room-runtime:2.5.1")
    kapt ("androidx.room:room-compiler:2.5.1")
    implementation ("androidx.room:room-ktx:2.5.1")

// DataStore (Preferences)
    implementation ("androidx.datastore:datastore-preferences:1.1.0")

// Hilt + Room + DataStore
    implementation ("com.google.dagger:hilt-android:2.46.1")
    kapt ("com.google.dagger:hilt-compiler:2.46.1")

    implementation ("com.google.accompanist:accompanist-pager:0.28.0")
    implementation ("com.google.accompanist:accompanist-pager-indicators:0.28.0")



}
