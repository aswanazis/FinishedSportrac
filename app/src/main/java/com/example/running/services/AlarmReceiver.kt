package com.example.running.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.running.R
import com.example.running.ui.activities.MainActivity
import com.example.running.ui.fragments.ScheduleFragment

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("AlarmReceiver", "Alarm received")

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "exercise_reminder"
            val channelName = "Exercise Reminder"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = "Channel for exercise reminders"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val scheduleId = intent.getIntExtra("scheduleId", -1)

        val notificationIntent = Intent(context, ScheduleFragment::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("scheduleId", scheduleId)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            scheduleId.hashCode(),
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notification = NotificationCompat.Builder(context, "exercise_reminder")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Waktunya Berolahraga!")
            .setContentText("Ingat untuk berolahraga sekarang.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setSound(soundUri)
            .build()

        notificationManager.notify(scheduleId.hashCode(), notification)
    }
}
