package com.example.running.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.running.db.Schedule
import com.example.running.repository.ScheduleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(private val scheduleRepository: ScheduleRepository) : ViewModel() {

    val allSchedules: LiveData<List<Schedule>> = scheduleRepository.getAllSchedules()

    fun getLastInsertedSchedule(): LiveData<Schedule?> {
        return scheduleRepository.getLastInsertedSchedule()
    }

    fun insert(schedule: Schedule, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                scheduleRepository.insert(schedule)
                callback(true)
            } catch (e: Exception) {
                Log.e("ScheduleViewModel", "Failed to insert schedule", e)
                callback(false)
            }
        }
    }

    fun update(schedule: Schedule) {
        viewModelScope.launch {
            scheduleRepository.update(schedule)
        }
    }

    fun delete(schedule: Schedule) {
        viewModelScope.launch {
            scheduleRepository.delete(schedule)
        }
    }

    fun deleteById(scheduleId: String) {
        viewModelScope.launch {
            scheduleRepository.deleteById(scheduleId)
        }
    }
}

