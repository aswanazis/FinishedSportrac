package com.example.running.db

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Run(
    var id: String? = null,
    val timestamp: Long = 0,
    val timeInMillis: Long = 0,
    val caloriesBurned: Int = 0,
    val avgSpeedInKMH: Float = 0f,
    val distanceInMeters: Int = 0,
    val imageUrl: String? = null
)
