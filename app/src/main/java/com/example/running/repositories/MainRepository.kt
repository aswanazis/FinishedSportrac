package com.example.running.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.running.db.Run
import com.example.running.db.RunDao
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val runDao: RunDao
) {
    suspend fun insertRun(run: Run) {
        runDao.insertRun(run)
    }
    suspend fun deleteRun(run: Run) = runDao.deleteRun(run)

    fun getAllRunsSortedByDate(): LiveData<List<Run>> = runDao.getAllRunsSortedByDate()

    fun getAllRunsSortedByDistance() = runDao.getAllRunsSortedByDistance()

    fun getAllRunsSortedByTimeInMillis() = runDao.getAllRunsSortedByTimeInMillis()

    fun getAllRunsSortedByAvgSpeed() = runDao.getAllRunsSortedByAvgSpeed()

    fun getAllRunsSortedByCaloriesBurned() = runDao.getAllRunsSortedByCaloriesBurned()

    fun getTotalAvgSpeed(): LiveData<Float> = runDao.getTotalAvgSpeed()

    fun getTotalDistance(): LiveData<Int> = runDao.getTotalDistance()

    fun getTotalCaloriesBurned(): LiveData<Int> = runDao.getTotalCaloriesBurned()

    fun getTotalTimeInMillis(): LiveData<Long> = runDao.getTotalTimeInMillis()

}
/*
why getAllRunsSortedByDate is not a suspend function?
-> we need a lifedata object and life data is asynchronous

Job of repo is to collect the data from the data source like room data or api
 */