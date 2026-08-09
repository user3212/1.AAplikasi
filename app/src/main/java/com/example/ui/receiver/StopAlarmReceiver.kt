package com.example.ui.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.ui.util.SoundHelper

class StopAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        SoundHelper.stopSound()
    }
}
