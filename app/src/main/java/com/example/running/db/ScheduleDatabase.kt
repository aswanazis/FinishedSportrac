package com.example.running.db

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope

class ScheduleDatabase private constructor() {

    private val db = FirebaseFirestoreInstance.getInstance()

    val scheduleDao: ScheduleDao by lazy {
        ScheduleDao(db)
    }

    companion object {
        @Volatile
        private var INSTANCE: ScheduleDatabase? = null

        fun getDatabase(context: Context): ScheduleDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = ScheduleDatabase()
                INSTANCE = instance
                instance
            }
        }
    }
}
