package com.example.running.db

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await

class RunDao(private val db: FirebaseFirestore) {

    private val runsCollection = db.collection("running_table")

    suspend fun insertRun(run: Run?) {
        run?.let {
            if (it.id.isNullOrBlank()) {
                try {
                    val documentRef = runsCollection.document()
                    it.id = documentRef.id
                    documentRef.set(it).await()
                    Log.d("RunDao", "Run berhasil ditambahkan ke Firestore. ID Dokumen: ${documentRef.id}")
                } catch (e: Exception) {
                    Log.e("RunDao", "Error saat menambahkan run ke Firestore", e)
                }
            } else {
                Log.e("RunDao", "Error: ID run tidak valid. ID run kosong atau null.")
            }
        } ?: run {
            Log.e("RunDao", "Error: Mencoba memasukkan objek run null.")
        }
    }

    suspend fun deleteRun(run: Run?) {
        run?.let {
            try {
                val querySnapshot = runsCollection
                    .whereEqualTo("id", it.id)
                    .get()
                    .await()

                querySnapshot.documents.forEach { document ->
                    runsCollection.document(document.id).delete()
                }
            } catch (e: Exception) {
                Log.e("RunDao", "Error deleting run from Firestore", e)
            }
        }
    }

    fun getAllRunsSortedByDate(): LiveData<List<Run>> {
        val liveData = MutableLiveData<List<Run>>()

        runsCollection
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w("RunDao", "Listen failed", error)
                    return@addSnapshotListener
                }

                snapshot?.let {
                    val runs = it.toObjects(Run::class.java)
                    liveData.value = runs
                }
            }

        return liveData
    }

    fun getTotalTimeInMillis(): LiveData<Long> {
        val liveData = MutableLiveData<Long>()
        runsCollection
            .get()
            .addOnSuccessListener { snapshot ->
                val total = snapshot.documents.sumOf { document ->
                    document.getLong("timeInMillis") ?: 0L
                }
                liveData.value = total
            }
            .addOnFailureListener { e ->
                Log.e("RunDao", "Error getting total time from Firestore", e)
            }
        return liveData
    }

    fun getTotalCaloriesBurned(): LiveData<Int> {
        val liveData = MutableLiveData<Int>()
        runsCollection
            .get()
            .addOnSuccessListener { snapshot ->
                val total = snapshot.documents.sumOf { document ->
                    document.getLong("caloriesBurned")?.toInt() ?: 0
                }
                liveData.value = total
            }
            .addOnFailureListener { e ->
                Log.e("RunDao", "Error getting total calories burned from Firestore", e)
            }
        return liveData
    }

    fun getTotalDistance(): LiveData<Int> {
        val liveData = MutableLiveData<Int>()
        runsCollection
            .get()
            .addOnSuccessListener { snapshot ->
                val total = snapshot.documents.sumOf { document ->
                    document.getLong("distanceInMeters")?.toInt() ?: 0
                }
                liveData.value = total
            }
            .addOnFailureListener { e ->
                Log.e("RunDao", "Error getting total distance from Firestore", e)
            }
        return liveData
    }

    fun getTotalAvgSpeed(): LiveData<Float> {
        val liveData = MutableLiveData<Float>()
        runsCollection
            .get()
            .addOnSuccessListener { snapshot ->
                val totalDistance = snapshot.documents.sumOf { document ->
                    document.getLong("distanceInMeters")?.toInt() ?: 0
                }
                val totalTime = snapshot.documents.sumOf { document ->
                    document.getLong("timeInMillis") ?: 0L
                }
                liveData.value = if (totalTime > 0) {
                    (totalDistance.toFloat() / totalTime) * 3600 // km/h
                } else {
                    0f
                }
            }
            .addOnFailureListener { e ->
                Log.e("RunDao", "Error getting total avg speed from Firestore", e)
            }
        return liveData
    }

    fun getAllRunsSortedByDistance(): LiveData<List<Run>> {
        val liveData = MutableLiveData<List<Run>>()
        runsCollection
            .orderBy("distanceInMeters")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w("RunDao", "Listen failed", error)
                    return@addSnapshotListener
                }

                snapshot?.let {
                    val runs = it.toObjects(Run::class.java)
                    liveData.value = runs
                }
            }
        return liveData
    }

    fun getAllRunsSortedByTimeInMillis(): LiveData<List<Run>> {
        val liveData = MutableLiveData<List<Run>>()
        runsCollection
            .orderBy("timeInMillis")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w("RunDao", "Listen failed", error)
                    return@addSnapshotListener
                }

                snapshot?.let {
                    val runs = it.toObjects(Run::class.java)
                    liveData.value = runs
                }
            }
        return liveData
    }

    fun getAllRunsSortedByAvgSpeed(): LiveData<List<Run>> {
        val liveData = MutableLiveData<List<Run>>()
        runsCollection
            .orderBy("avgSpeedInKMH")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w("RunDao", "Listen failed", error)
                    return@addSnapshotListener
                }

                snapshot?.let {
                    val runs = it.toObjects(Run::class.java)
                    liveData.value = runs
                }
            }
        return liveData
    }

    fun getAllRunsSortedByCaloriesBurned(): LiveData<List<Run>> {
        val liveData = MutableLiveData<List<Run>>()
        runsCollection
            .orderBy("caloriesBurned")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w("RunDao", "Listen failed", error)
                    return@addSnapshotListener
                }

                snapshot?.let {
                    val runs = it.toObjects(Run::class.java)
                    liveData.value = runs
                }
            }
        return liveData
    }

}
