package com.example.ui.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.ui.receiver.AlarmReceiver
import com.example.ui.viewmodel.HariJadwal
import java.util.Calendar

object AlarmScheduler {
    fun scheduleAlarms(context: Context, jadwalList: List<HariJadwal>, soundName: String, repetition: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        // Cancel all existing alarms first to avoid duplicates
        for (dayId in 1..7) {
            for (jamId in 1..20) {
                for (type in listOf("now", "pre")) {
                    val intent = Intent(context, AlarmReceiver::class.java)
                    val reqCode = (dayId * 1000) + (jamId * 10) + (if (type == "now") 1 else 0)
                    val pendingIntent = PendingIntent.getBroadcast(
                        context, reqCode, intent,
                        PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
                    )
                    if (pendingIntent != null) {
                        alarmManager.cancel(pendingIntent)
                        pendingIntent.cancel()
                    }
                }
            }
        }

        // Schedule new alarms
        jadwalList.forEachIndexed { dayIndex, jadwal ->
            val dayOfWeek = when (jadwal.hari.lowercase()) {
                "minggu" -> Calendar.SUNDAY
                "senin" -> Calendar.MONDAY
                "selasa" -> Calendar.TUESDAY
                "rabu" -> Calendar.WEDNESDAY
                "kamis" -> Calendar.THURSDAY
                "jumat" -> Calendar.FRIDAY
                "sabtu" -> Calendar.SATURDAY
                else -> -1
            }
            if (dayOfWeek != -1) {
                jadwal.jamList.forEachIndexed { jamIndex, jam ->
                    val parts = jam.waktuMulai.split(":")
                    if (parts.size == 2) {
                        val h = parts[0].toIntOrNull()
                        val m = parts[1].toIntOrNull()
                        if (h != null && m != null) {
                            
                            // "Now" Alarm
                            scheduleSingleAlarm(
                                context = context,
                                alarmManager = alarmManager,
                                dayOfWeek = dayOfWeek,
                                hour = h,
                                minute = m,
                                offsetMinutes = 0,
                                title = "Jam Mengajar Dimulai!",
                                message = "Waktunya masuk kelas untuk jadwal ${jadwal.hari} jam ${jam.waktuMulai}",
                                soundName = soundName,
                                repetition = repetition,
                                reqCode = (dayIndex * 1000) + (jamIndex * 10) + 1
                            )

                            // "Pre" Alarm (10 mins before)
                            scheduleSingleAlarm(
                                context = context,
                                alarmManager = alarmManager,
                                dayOfWeek = dayOfWeek,
                                hour = h,
                                minute = m,
                                offsetMinutes = -10,
                                title = "Persiapan Mengajar",
                                message = "10 menit lagi kelas akan dimulai (${jam.waktuMulai}).",
                                soundName = soundName,
                                repetition = 1, // Pre-alarm usually rings once
                                reqCode = (dayIndex * 1000) + (jamIndex * 10) + 0
                            )
                        }
                    }
                }
            }
        }
    }

    private fun scheduleSingleAlarm(
        context: Context, alarmManager: AlarmManager, 
        dayOfWeek: Int, hour: Int, minute: Int, offsetMinutes: Int,
        title: String, message: String, 
        soundName: String, repetition: Int, reqCode: Int
    ) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("title", title)
            putExtra("message", message)
            putExtra("soundName", soundName)
            putExtra("repetition", repetition)
            putExtra("reqCode", reqCode)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context, reqCode, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        
        // Find the next occurrence of dayOfWeek
        var daysUntil = (dayOfWeek - calendar.get(Calendar.DAY_OF_WEEK))
        if (daysUntil < 0) {
            daysUntil += 7
        }
        calendar.add(Calendar.DAY_OF_YEAR, daysUntil)
        
        // Apply offset (e.g. -10 mins for pre-alarm)
        calendar.add(Calendar.MINUTE, offsetMinutes)

        // If the resulting time is in the past, add 7 days (next week)
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 7)
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
                } else {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
