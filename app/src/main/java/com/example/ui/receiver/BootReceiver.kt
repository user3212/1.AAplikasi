package com.example.ui.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.ui.util.AlarmScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import com.example.ui.viewmodel.HariJadwal
import com.example.ui.viewmodel.JamMengajar

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == "android.intent.action.QUICKBOOT_POWERON") {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val prefs = context.getSharedPreferences("pesantren_prefs", Context.MODE_PRIVATE)
                    val jadwalJson = prefs.getString("jadwal_data", "[]") ?: "[]"
                    
                    val jsonArray = JSONArray(jadwalJson)
                    val list = mutableListOf<HariJadwal>()
                    for (i in 0 until jsonArray.length()) {
                        val dayObj = jsonArray.getJSONObject(i)
                        val day = HariJadwal(
                            id = dayObj.getString("id"), 
                            hari = dayObj.getString("hari"),
                            isExpanded = dayObj.optBoolean("isExpanded", false),
                            jamList = mutableListOf()
                        )
                        val jamArray = dayObj.getJSONArray("jamList")
                        for (j in 0 until jamArray.length()) {
                            val jamObj = jamArray.getJSONObject(j)
                            day.jamList.add(
                                JamMengajar(
                                    id = jamObj.getString("id"),
                                    jamKe = jamObj.optString("jamKe", ""),
                                    waktuMulai = jamObj.getString("waktuMulai"),
                                    waktuSelesai = jamObj.getString("waktuSelesai")
                                )
                            )
                        }
                        list.add(day)
                    }
                    
                    val sound = prefs.getString("jadwal_sound", "Sound 1") ?: "Sound 1"
                    val repetition = prefs.getInt("jadwal_repetition", 1)
                    
                    AlarmScheduler.scheduleAlarms(context, list, sound, repetition)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
