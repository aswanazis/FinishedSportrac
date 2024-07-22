plugins {
    kotlin("kapt")
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.gms.google-services")
    id ("dagger.hilt.android.plugin")

}


android {
    compileSdk = 34
    namespace = "com.example.running"

    defaultConfig {
        applicationId = "com.example.running"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"

    }

    buildFeatures {
        compose = true
    }

    kapt {
        correctErrorTypes = true
        useBuildCache = true

    }

    viewBinding {
        enable = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}


dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.firebase.firestore)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.lifecycle.service)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Material Design
    implementation("com.google.android.material:material:1.5.0")

    // Architectural Components
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0")

    // Room
    implementation("androidx.room:room-runtime:2.4.1")
    kapt("androidx.room:room-compiler:2.4.1")

    // Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:2.3.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Kotlin Standard Library
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.6.10")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.6.10")
    kapt ("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.5.0")

    // Coroutine Lifecycle Scopes
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:2.8.2")

    // Navigation Components
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    implementation ("androidx.navigation:navigation-compose:2.7.7")

    implementation ("androidx.hilt:hilt-navigation-compose:1.0.0")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    kapt("com.github.bumptech.glide:compiler:4.16.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")

    // Google Maps Location Services
    implementation("com.google.android.gms:play-services-location:17.0.0")
    implementation("com.google.android.gms:play-services-maps:17.0.0")

    // Dagger Core
    implementation(libs.dagger)
    kapt(libs.dagger.compiler)

    // Activity KTX for viewModels()
    implementation("androidx.activity:activity-ktx:1.4.0")

    // Dagger - Hilt
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-android-compiler:2.48")

    // Easy Permissions
    implementation("pub.devrel:easypermissions:3.0.0")


    // Timber
    implementation("com.jakewharton.timber:timber:4.7.1")

    // Lottie
    val lottieVersion = "3.7.2"
    implementation("com.airbnb.android:lottie:$lottieVersion")

    // Firebase
    implementation("com.firebaseui:firebase-ui-auth:7.2.0")
    implementation(platform("com.google.firebase:firebase-bom:32.2.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.android.gms:play-services-auth:19.2.0")
    implementation ("com.google.firebase:firebase-storage:20.0.1")
    implementation ("com.google.firebase:firebase-appcheck-playintegrity:16.0.0")


}
