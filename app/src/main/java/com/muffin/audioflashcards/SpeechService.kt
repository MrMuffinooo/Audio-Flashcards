package com.muffin.audioflashcards

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class SpeechService: Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotifficationChannel()

        val notifIntent = Intent(this, LearnActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notifIntent, 0)

        val notif = NotificationCompat.Builder(this, "SpeechChannelId")
                .setContentTitle("Audio Flashcards")
                .setSmallIcon(R.drawable.ic_baseline_speaker_24)
                .setContentIntent(pendingIntent)    //should go back to activity but makes new one instead
                .build()
        startForeground(1,notif)
        return START_NOT_STICKY
    }

    private fun createNotifficationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                    "SpeechChannelId", "Foreground", NotificationManager.IMPORTANCE_DEFAULT)
            val manager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}