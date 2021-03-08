package com.muffin.audioflashcards

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson

class LearnActivity : AppCompatActivity() {

    lateinit var mService: SpeechService
    var isBound = false

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



        val play = findViewById<ImageButton>(R.id.btn_start)
        val stop = findViewById<ImageButton>(R.id.btn_pause)

        play.visibility = View.INVISIBLE
        play.isEnabled = false
        stop.visibility = View.VISIBLE
        stop.isEnabled = true



        val intent = Intent(this, SpeechService::class.java)
        startService(intent)    //maybe not needed ????
        bindService(intent,serviceConnection,Context.BIND_AUTO_CREATE)

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
    }

    override fun onDestroy() {
        val intent = Intent(this, SpeechService::class.java)
        if (isBound && mService != null)
            unbindService(serviceConnection)
        stopService(intent)
        super.onDestroy()
    }
}