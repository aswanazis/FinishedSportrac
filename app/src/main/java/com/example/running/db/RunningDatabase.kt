package com.example.running.db

import com.google.firebase.firestore.FirebaseFirestore

class RunningDatabase private constructor() {

    private val db = FirebaseFirestoreInstance.getInstance()

    fun getRunDao(): RunDao {
        return RunDao(db)
    }

    companion object {
        @Volatile
        private var INSTANCE: RunningDatabase? = null

        fun getInstance(): RunningDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = RunningDatabase()
                INSTANCE = instance
                instance
            }
        }
    }
}
