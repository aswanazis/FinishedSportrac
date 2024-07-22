package com.example.running.repository

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.running.db.Schedule
import com.example.running.db.ScheduleDao
import javax.inject.Inject

class ScheduleRepository @Inject constructor(
    private val scheduleDao: ScheduleDao
) {

    fun getAllSchedules(): LiveData<List<Schedule>> {
        return scheduleDao.getAllSchedules()
    }

    fun getLastInsertedSchedule(): LiveData<Schedule?> {
        return scheduleDao.getLastInsertedSchedule()
    }

    suspend fun insert(schedule: Schedule) {
        try {
            scheduleDao.insert(schedule)
            Log.d(TAG, "Insert success")
        } catch (e: Exception) {
            Log.e(TAG, "Insert failed", e)
        }
    }


    suspend fun update(schedule: Schedule) {
        scheduleDao.update(schedule)
    }

    suspend fun delete(schedule: Schedule) {
        scheduleDao.delete(schedule)
    }

    suspend fun deleteById(scheduleId: String) {
        scheduleDao.deleteById(scheduleId)
    }
}
