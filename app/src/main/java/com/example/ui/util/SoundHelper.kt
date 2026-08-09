package com.example.ui.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.media.ToneGenerator
import android.os.Build
import android.os.Handler
import android.os.Looper

object SoundHelper {
    private var activeRingtone: Ringtone? = null
    private var activeToneGenerator: ToneGenerator? = null
    private var isPlaying = false
    private val handler = Handler(Looper.getMainLooper())
    private var stopRunnable: Runnable? = null

    fun playSound(context: Context, soundName: String = "Nada 1", repetition: Int = 60, onFinished: (() -> Unit)? = null) {
        stopSound()
        isPlaying = true
        try {
            // 1. Try system RingtoneManager with ALARM / NOTIFICATION attributes
            val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            if (uri != null) {
                val ringtone = RingtoneManager.getRingtone(context, uri)
                if (ringtone != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        ringtone.isLooping = true
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ringtone.audioAttributes = AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    }
                    activeRingtone = ringtone
                    ringtone.play()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 2. Play distinct ToneGenerator beeps guaranteed on all hardware/RAM/speakers
        val toneType = when (soundName) {
            "Nada 2" -> ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD
            "Nada 3" -> ToneGenerator.TONE_PROP_BEEP
            else -> ToneGenerator.TONE_CDMA_ABBR_ALERT // Nada 1
        }

        Thread {
            try {
                val toneGen = try {
                    ToneGenerator(AudioManager.STREAM_ALARM, 100)
                } catch (e: Exception) {
                    ToneGenerator(AudioManager.STREAM_MUSIC, 100)
                }
                activeToneGenerator = toneGen
                
                // loop for 60 seconds
                for (i in 0 until 50) {
                    if (!isPlaying) break
                    toneGen.startTone(toneType, 800) // 800ms per tone
                    Thread.sleep(1200)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                handler.post {
                    if (isPlaying) {
                        stopSound()
                        onFinished?.invoke()
                    }
                }
            }
        }.start()

        // Safety stop timer after 60s max
        stopRunnable = Runnable {
            stopSound()
            onFinished?.invoke()
        }
        handler.postDelayed(stopRunnable!!, 60000)
    }

    fun stopSound() {
        isPlaying = false
        stopRunnable?.let { handler.removeCallbacks(it) }
        stopRunnable = null
        try {
            activeRingtone?.stop()
            activeRingtone = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            activeToneGenerator?.release()
            activeToneGenerator = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isCurrentlyPlaying(): Boolean = isPlaying
}
