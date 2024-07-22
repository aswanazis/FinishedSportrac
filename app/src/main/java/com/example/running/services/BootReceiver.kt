package com.example.running.services

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.running.db.Schedule
import com.example.running.db.ScheduleDao
import com.example.running.db.ScheduleDatabase
import com.example.running.repository.ScheduleRepository
import java.util.Calendar

class BootReceiver : BroadcastReceiver() {

    private lateinit var scheduleDao: ScheduleDao
    private lateinit var repository: ScheduleRepository

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val database = ScheduleDatabase.getDatabase(context)
            scheduleDao = database.scheduleDao
            repository = ScheduleRepository(scheduleDao)

            repository.getAllSchedules().observeForever { allSchedules ->
                allSchedules?.forEach { schedule ->
                    Log.d("BootReceiver", "Resetting alarm for schedule: $schedule")
                    setAlarm(context, schedule)
                }
            }
        }
    }

    private fun setAlarm(context: Context, schedule: Schedule) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, schedule.hour)
            set(Calendar.MINUTE, schedule.minute)
            set(Calendar.SECOND, 0)
        }
        Log.d("BootReceiver", "Alarm set for: ${calendar.time}")

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("scheduleId", schedule.id)
        }

        val pendingIntent = schedule.id?.let {
            PendingIntent.getBroadcast(
                context,
                it.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val canScheduleExact = alarmManager.canScheduleExactAlarms()
            if (canScheduleExact) {
                pendingIntent?.let {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, it)
                }
            } else {
                Log.e("BootReceiver", "Cannot schedule exact alarms. Consider requesting permission.")
                pendingIntent?.let {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, it)
                }
            }
        } else {
            pendingIntent?.let {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, it)
            }
        }
    }
}
