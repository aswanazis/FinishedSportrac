package com.example.running.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.running.db.Run
import com.example.running.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val savedStateHandle: SavedStateHandle

) : ViewModel() {

    val totalTimeRun: LiveData<Long> = mainRepository.getTotalTimeInMillis()
    val totalDistance: LiveData<Int> = mainRepository.getTotalDistance()
    val totalCaloriesBurned: LiveData<Int> = mainRepository.getTotalCaloriesBurned()
    val totalAvgSpeed: LiveData<Float> = mainRepository.getTotalAvgSpeed()
    val runsSortedByDate: LiveData<List<Run>> = mainRepository.getAllRunsSortedByDate()
}

