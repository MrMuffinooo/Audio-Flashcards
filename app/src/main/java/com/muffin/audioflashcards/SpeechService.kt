package com.muffin.audioflashcards

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.speech.tts.TextToSpeech
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.gson.Gson
import java.util.*

class SpeechService: Service() {
    private val binder = LocalBinder()
    var progress = 0
    var maxValue = 10
    var isPaused = false
    lateinit var TTS_JP: TextToSpeech
    val broadcaster = LocalBroadcastManager.getInstance(this);
    companion object {
        const val UI_broadcast = "UI_Update"
    }


    lateinit var set: FlashcardsStorage


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotifficationChannel()
        makeNotification()
        setParamFromPreferences()

        TTS_JP = TextToSpeech(this, object : TextToSpeech.OnInitListener {
            override fun onInit(status: Int) {
                if (status == TextToSpeech.SUCCESS) {
                    var result = TTS_JP.setLanguage(Locale.JAPANESE)

                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        TODO("install lang")
                    }
                } else {
                    stopSelf()
                }

            }
        })


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

    private fun makeNotification() {
        val notifIntent = Intent(this, LearnActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notifIntent, 0)

        val notif = NotificationCompat.Builder(this, "SpeechChannelId")
                .setContentTitle("Audio Flashcards")
                .setSmallIcon(R.drawable.ic_baseline_speaker_24)
                .setContentIntent(pendingIntent)    //should go back to activity but makes new one instead
                .build()
        startForeground(1, notif)
    }

    fun setParamFromPreferences(){
        val prefs = getSharedPreferences("flashcards", Context.MODE_PRIVATE)
        val gson = Gson()
        val json: String? = prefs.getString("list", "")
        val list = gson.fromJson(json, FlashcardsStorage::class.java)

        set = list.getSetToListen()

        maxValue = set.getSize()
    }



    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    inner class LocalBinder : Binder() {
        fun getService():SpeechService{
            return this@SpeechService
        }
    }

    fun resumeReading(){

        while (!isPaused){
            var f = set.get(progress)

            //TODO("play flashcards")
            sendResult("FLASHCARD", f.word, f.translation, f.extra)
            sendResult("TRANSLATION")
            //sendResult("EXTRA")
            progress++
            if (progress > maxValue)
                progress = 0
        }
    }

    fun pauseReading(){
        isPaused = true
        TODO("halt reading")
    }

    fun skipNext(){
        //TODO("halt reading")
        if (progress == maxValue)
            progress = 0
        else
            progress++
    }
    fun skipPrev(){
       // TODO("halt reading")
        if (progress == 0)
            progress = maxValue
        else
            progress--
    }

    fun sendResult(message: String, word: String ="", trans: String ="", extra: String ="") {
        val intent = Intent(UI_broadcast)
        intent.putExtra(UI_broadcast, message)

            intent.putExtra("WORD",word)
            intent.putExtra("TRANSLATION",trans)
            intent.putExtra("EXTRA",extra)


        broadcaster.sendBroadcast(intent)
    }


}