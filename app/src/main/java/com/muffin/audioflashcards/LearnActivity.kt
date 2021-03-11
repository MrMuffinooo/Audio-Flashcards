package com.muffin.audioflashcards

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager


class LearnActivity : AppCompatActivity() {

    lateinit var progress: ProgressBar
    lateinit var mService: SpeechService
    var isBound = false
    lateinit var receiver: BroadcastReceiver
    lateinit var txt_word: TextView
    lateinit var txt_trans: TextView

    val serviceConnection = object: ServiceConnection{
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            mService = (service as SpeechService.LocalBinder).getService()
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            finish()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learn)


        txt_word = findViewById(R.id.txt_to_translate_audio)
        txt_trans = findViewById(R.id.txt_translated_audio)
        progress = findViewById(R.id.progressBar)

        val play = findViewById<ImageButton>(R.id.btn_start)
        val stop = findViewById<ImageButton>(R.id.btn_pause)

        play.visibility = View.INVISIBLE
        play.isEnabled = false
        stop.visibility = View.VISIBLE
        stop.isEnabled = true

        var fadeIn = AlphaAnimation(0.5f,1f)
        fadeIn.duration = 500

        var fadeOut = AlphaAnimation(1f,0.5f)
        fadeOut.duration = 500





        val intent = Intent(this, SpeechService::class.java)
        startService(intent)    //maybe not needed ????
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)

        play.setOnClickListener {
            play.visibility = View.INVISIBLE
            play.isEnabled = false
            stop.visibility = View.VISIBLE
            stop.isEnabled = true
            mService.resumeReading()
        }

        stop.setOnClickListener {
            play.visibility = View.VISIBLE
            play.isEnabled = true
            stop.visibility = View.INVISIBLE
            stop.isEnabled = false
            mService.pauseReading()
        }
        findViewById<ImageButton>(R.id.btn_next).setOnClickListener{
            mService.skipNext()
        }
        findViewById<ImageButton>(R.id.btn_prev).setOnClickListener{
            mService.skipPrev()
        }

        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val s = intent.getStringExtra(SpeechService.UI_broadcast)
                when(s){
                    "FLASHCARD" -> nextFlashcard(
                        intent.getStringExtra("WORD")!!,
                        intent.getStringExtra("TRANSLATION")!!,
                        intent.getStringExtra("EXTRA")!!
                    )

                    "TRANSLATION" -> {
                        txt_word.startAnimation(fadeOut)
                        txt_trans.startAnimation(fadeIn)
                    }

                    "EXTRA" -> {

                    }
                }
            }
        }
        mService.resumeReading()
    }

    fun nextFlashcard(W: String, T: String, E: String) {
        txt_word.text = W
        txt_trans.text = T
        //TODO("extra")
        progress.progress++
        if (progress.progress > progress.max)
            progress.progress = 0
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            receiver,
            IntentFilter(SpeechService.UI_broadcast)
        )
    }

    override fun onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
        super.onStop()
    }

    override fun onDestroy() {
        val intent = Intent(this, SpeechService::class.java)
        if (isBound && mService != null)
            unbindService(serviceConnection)
        stopService(intent)
        super.onDestroy()
    }
}