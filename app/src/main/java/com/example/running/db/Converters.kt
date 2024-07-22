package com.example.running.db

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromTimestamp(value: Long?): Long? {
        return value
    }

    @TypeConverter
    fun dateToTimestamp(date: Long?): Long? {
        return date
    }
}
