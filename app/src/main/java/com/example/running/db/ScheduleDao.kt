package com.example.running.db

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ScheduleDao @Inject constructor(private val db: FirebaseFirestore) {
    val collection = db.collection("schedule_table")

    suspend fun insert(schedule: Schedule): Boolean {
        return try {
            val docRef = if (schedule.id != null) {
                collection.document(schedule.id!!)
            } else {
                collection.document()
            }

            val scheduleWithId = schedule.copy(id = docRef.id)
            docRef.set(scheduleWithId).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun getLastInsertedSchedule(): LiveData<Schedule?> {
        val liveData = MutableLiveData<Schedule?>()
        collection.orderBy("id").limitToLast(1).addSnapshotListener { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }
            if (snapshot != null && !snapshot.isEmpty) {
                val schedule = snapshot.documents[0].toObject(Schedule::class.java)
                liveData.value = schedule
            } else {
                liveData.value = null
            }
        }
        return liveData
    }

    suspend fun delete(schedule: Schedule) {
        try {
            schedule.id?.let { collection.document(it).delete().await() }
        } catch (e: Exception) {
            throw ScheduleDeletionException("Failed to delete schedule", e)
        }
    }

    fun getAllSchedules(): LiveData<List<Schedule>> {
        val liveData = MutableLiveData<List<Schedule>>()
        collection.orderBy("id").addSnapshotListener { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val schedules = snapshot.toObjects(Schedule::class.java)
                liveData.value = schedules
            }
        }
        return liveData
    }

    suspend fun deleteById(scheduleId: String) {
        try {
            collection.document(scheduleId).delete().await()
        } catch (e: Exception) {
            throw ScheduleDeletionException("Failed to delete schedule with id $scheduleId", e)
        }
    }


    suspend fun update(schedule: Schedule) {
        try {
            schedule.id?.let { collection.document(it).set(schedule).await() }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating document", e)
        }
    }
}

class ScheduleInsertionException(message: String, cause: Throwable) : Exception(message, cause)

class ScheduleDeletionException(message: String, cause: Throwable) : Exception(message, cause)

class ScheduleUpdateException(message: String, cause: Throwable) : Exception(message, cause)
