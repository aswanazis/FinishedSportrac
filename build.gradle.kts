plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.google.gms.google.services) apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
}

buildscript {

    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.4.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
        classpath("com.google.gms:google-services:4.4.2")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.7")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.48")
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}