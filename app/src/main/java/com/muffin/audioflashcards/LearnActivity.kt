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
    lateinit var fadeOut: AlphaAnimation
    lateinit var fadeIn: AlphaAnimation

    lateinit var serviceConnection: ServiceConnection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learn)

        var serviceConnection: ServiceConnection = object: ServiceConnection{
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                mService = (service as SpeechService.LocalBinder).getService()
                isBound = true
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                finish()
            }

        }


        txt_word = findViewById(R.id.txt_to_translate_audio)
        txt_trans = findViewById(R.id.txt_translated_audio)
        progress = findViewById(R.id.progressBar)

        val play = findViewById<ImageButton>(R.id.btn_start)
        val stop = findViewById<ImageButton>(R.id.btn_pause)

        play.visibility = View.INVISIBLE
        play.isEnabled = false
        stop.visibility = View.VISIBLE
        stop.isEnabled = true

        fadeIn = AlphaAnimation(0.5f,1f)
        fadeIn.duration = 500

        fadeOut = AlphaAnimation(1f,0.5f)
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
                if (intent.action == null || !intent.action.equals("UI_Update"))
                    return
                val s = intent.getStringExtra(SpeechService.UI_broadcast)
                when(s){
                    "FLASHCARD" ->
                        nextFlashcard(
                                intent.getStringExtra("WORD")!!,
                                intent.getStringExtra("TRANSLATION")!!,
                                intent.getStringExtra("EXTRA")!!,
                                intent.getIntExtra("PROGRESS",0)!!
                        )

                    "TRANSLATION" -> {
                        //txt_word.startAnimation(fadeOut)
                        txt_word.alpha = 0.5f
                        //txt_trans.startAnimation(fadeIn)
                        txt_trans.alpha = 1f
                    }

                    "EXTRA" -> {

                    }
                }
            }
        }

    }

    fun nextFlashcard(W: String, T: String, E: String, P :Int) {
        txt_word.text = W
        txt_trans.text = T
        progress.progress = P
        //txt_word.startAnimation(fadeIn)
        txt_word.alpha = 1f
        //txt_trans.startAnimation(fadeOut)
        txt_trans.alpha = 0.5f
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            receiver,
            IntentFilter(SpeechService.UI_broadcast)
        )
    }


    override fun onDestroy() {
        val intent = Intent(this, SpeechService::class.java)
//        if (isBound && mService != null)
  //          unbindService(serviceConnection)
        stopService(intent)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
        super.onDestroy()
    }
}