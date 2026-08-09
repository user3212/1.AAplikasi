package com.example.ui.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.R
import com.example.ui.receiver.AlarmActivity
import com.example.ui.receiver.StopAlarmReceiver

object NotificationHelper {
    private const val CHANNEL_ID = "jadwal_channel_high"
    
    fun showNotification(context: Context, title: String, message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Jadwal Mengajar (Penting)",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifikasi Pengingat Jadwal"
                setShowBadge(false) // Hapus icon angka notifikasi (badge)
            }
            notificationManager.createNotificationChannel(channel)
        }
        
        // Full screen intent (Activity yang muncul popup di tengah)
        val fullScreenIntent = Intent(context, AlarmActivity::class.java).apply {
            putExtra("title", title)
            putExtra("message", message)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val fullScreenPendingIntent = PendingIntent.getActivity(
            context, 0, fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Delete intent to stop sound when swiped away
        val deleteIntent = Intent(context, StopAlarmReceiver::class.java)
        val deletePendingIntent = PendingIntent.getBroadcast(
            context, 0, deleteIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setDeleteIntent(deletePendingIntent)
            .setAutoCancel(true)
            .setOngoing(false) 

        notificationManager.notify(1001, builder.build())
    }
}
