package com.example.running.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import com.example.running.db.RunDao
import com.example.running.db.RunningDatabase
import com.example.running.extras.Constants.KEY_FIRST_TIME_TOGGLE
import com.example.running.extras.Constants.KEY_NAME
import com.example.running.extras.Constants.KEY_WEIGHT
import com.example.running.extras.Constants.RUNNING_DATABASE_NAME
import com.example.running.extras.Constants.SHARED_PREFERENCES_NAME
import com.example.running.repositories.MainRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Singleton
    @Provides
    fun provideFirebaseFirestoreRunDao(db: FirebaseFirestore): RunDao {
        return RunDao(db)
    }

    @Singleton
    @Provides
    fun provideMainRepository(runDao: RunDao): MainRepository {
        return MainRepository(runDao)
    }

    @Singleton
    @Provides
    fun providesSharedPreferences(@ApplicationContext app: Context): SharedPreferences {
        return app.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Singleton
    @Provides
    fun provideName(sharedPref: SharedPreferences): String {
        return sharedPref.getString(KEY_NAME, "") ?: ""
    }

    @Singleton
    @Provides
    fun provideWeight(sharedPref: SharedPreferences): Float {
        return sharedPref.getFloat(KEY_WEIGHT, 80f)
    }

    @Singleton
    @Provides
    fun provideFirstTimeToggle(sharedPref: SharedPreferences): Boolean {
        return sharedPref.getBoolean(KEY_FIRST_TIME_TOGGLE, true)
    }
}
