package com.example.running.db

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Schedule(
    var id: String? = null,
    val hour: Int = 0,
    val minute: Int = 0,
    var createdAt: Long = System.currentTimeMillis()
)

