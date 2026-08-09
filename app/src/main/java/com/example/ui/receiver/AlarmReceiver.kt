package com.example.ui.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.ui.util.NotificationHelper
import com.example.ui.util.SoundHelper
import android.app.AlarmManager
import android.app.PendingIntent
import android.os.Build
import java.util.Calendar

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "Jam Mengajar"
        val message = intent.getStringExtra("message") ?: "Waktunya masuk kelas!"
        val soundName = intent.getStringExtra("soundName") ?: "Nada 1"
        val repetition = intent.getIntExtra("repetition", 2)
        
        // Show notification and play sound
        NotificationHelper.showNotification(context, title, message)
        SoundHelper.playSound(context, soundName, repetition)

        // Reschedule for next week
        val reqCode = intent.getIntExtra("reqCode", -1)
        if (reqCode != -1) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val newIntent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra("title", title)
                putExtra("message", message)
                putExtra("soundName", soundName)
                putExtra("repetition", repetition)
                putExtra("reqCode", reqCode)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context, reqCode, newIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val nextWeek = Calendar.getInstance().apply {
                add(Calendar.WEEK_OF_YEAR, 1)
            }.timeInMillis

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextWeek, pendingIntent)
                    } else {
                        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextWeek, pendingIntent)
                    }
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextWeek, pendingIntent)
                } else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, nextWeek, pendingIntent)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
