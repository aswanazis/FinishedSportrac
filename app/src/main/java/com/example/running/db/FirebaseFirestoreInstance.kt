package com.example.running.db

import com.google.firebase.firestore.FirebaseFirestore

object FirebaseFirestoreInstance {
    @Volatile
    private var INSTANCE: FirebaseFirestore? = null

    fun getInstance(): FirebaseFirestore {
        return INSTANCE ?: synchronized(this) {
            val instance = FirebaseFirestore.getInstance()
            INSTANCE = instance
            instance
        }
    }
}
