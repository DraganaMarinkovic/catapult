// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // Android Gradle Plugin
        classpath("com.android.tools.build:gradle:7.4.2")
        // Kotlin Gradle Plugin
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.10")
        // Hilt Gradle Plugin
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.46.1")
        // Kotlinx Serialization Plugin
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.9.10")
    }
}


