package com.muffin.audioflashcards

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.*
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.gson.Gson
import java.util.*

class SpeechService: Service() {
    private val binder = LocalBinder()
    var prog = 0
    var maxValue = 9
    var isPaused = false
    lateinit var TTS_JP: TextToSpeech
    lateinit var TTS_LOC: TextToSpeech
    var SILENCE_DURATION: Long = 2000
    val broadcaster = LocalBroadcastManager.getInstance(this)
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
        TTS_LOC = TextToSpeech(this, object : TextToSpeech.OnInitListener {
            override fun onInit(status: Int) {
                if (status == TextToSpeech.SUCCESS) {
                    var result = TTS_LOC.setLanguage(Locale.getDefault())

                    if (result == TextToSpeech.LANG_MISSING_DATA
                        || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        TODO("install lang")
                    }
                    else{
                        Handler(Looper.getMainLooper()).postDelayed({
                            resumeReading()
                        }, 1000)
                    }
                } else {
                    stopSelf()
                }

            }
        })

        TTS_LOC.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                if (isPaused){
                    stopSelf()
                    return
                }
                if (utteranceId != "silence") {
                    var f = set.get(prog)
                    sendResult("FLASHCARD", f.word, f.translation, f.extra, prog)
                }
            }

            override fun onDone(utteranceId: String?) {
                if (isPaused){
                    stopSelf()
                    return
                }
                if (utteranceId != "silence"){
                    TTS_LOC.playSilentUtterance(SILENCE_DURATION, TextToSpeech.QUEUE_FLUSH, "silence")
                }
                else{
                    var f = set.get(prog)
                    TTS_JP.speak(f.translation, TextToSpeech.QUEUE_FLUSH, null, "trans")
                }
            }

            override fun onError(utteranceId: String?) {
                isPaused = true
            }

        })

        TTS_JP.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                if (isPaused){
                    stopSelf()
                    return
                }
                if (utteranceId != "silence") {
                    sendResult("TRANSLATION")
                }
            }

            override fun onDone(utteranceId: String?) {
                if (isPaused){
                    stopSelf()
                    return
                }
                if (utteranceId != "silence"){
                    TTS_JP.playSilentUtterance(SILENCE_DURATION, TextToSpeech.QUEUE_FLUSH, "silence")
                }
                else{
                    prog++
                    if (prog > maxValue)
                        prog = 0
                    var f = set.get(prog)
                    TTS_LOC.speak(f.word, TextToSpeech.QUEUE_FLUSH, null, "word")
                }
            }

            override fun onError(utteranceId: String?) {
                isPaused = true
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

        maxValue = set.getSize()-1
        //TODO("pass maxVal to activity")
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
        isPaused = false
        var f = set.get(prog)
        TTS_LOC.speak(f.word, TextToSpeech.QUEUE_FLUSH, null, "word")
    }

    fun pauseReading(){
        isPaused = true
        TTS_LOC.stop()
        TTS_JP.stop()
    }

    fun skipNext(){
        if (prog == maxValue)
            prog = 0
        else
            prog++
        if (TTS_JP.isSpeaking || TTS_LOC.isSpeaking) {
            TTS_LOC.stop()
            TTS_JP.stop()
            resumeReading()
        }
        else{
            var f = set.get(prog)
            sendResult("FLASHCARD", f.word, f.translation, f.extra, prog)
        }
    }
    fun skipPrev(){
        if (prog == 0)
            prog = maxValue
        else
            prog--
        if (TTS_JP.isSpeaking || TTS_LOC.isSpeaking) {
            TTS_LOC.stop()
            TTS_JP.stop()
            resumeReading()
        }
        else{
            var f = set.get(prog)
            sendResult("FLASHCARD", f.word, f.translation, f.extra, prog)
        }

    }

    fun sendResult(message: String, word: String ="", trans: String ="", extra: String ="", progress: Int = 0) {
        val intent = Intent(UI_broadcast)
        intent.putExtra(UI_broadcast, message)

            intent.putExtra("WORD",word)
            intent.putExtra("TRANSLATION",trans)
            intent.putExtra("EXTRA",extra)
            intent.putExtra("PROGRESS",progress)


        broadcaster.sendBroadcast(intent)
    }

    override fun onDestroy() {
        if (TTS_JP != null){
            TTS_JP.stop()
            TTS_JP.shutdown()
        }
        if (TTS_LOC != null){
            TTS_LOC.stop()
            TTS_LOC.shutdown()
        }
        super.onDestroy()
    }
}

