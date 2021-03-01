package com.muffin.audioflashcards

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson

class LearnActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learn)

        val prefs = getSharedPreferences("flashcards", Context.MODE_PRIVATE)
        val prefsEditor = prefs.edit()
        val gson = Gson()
        val json: String? = prefs.getString("list", "")
        val list: FlashcardsStorage = gson.fromJson(json, FlashcardsStorage::class.java)

        var set = list.getSetToListen()

        val play = findViewById<ImageButton>(R.id.btn_start)
        val stop = findViewById<ImageButton>(R.id.btn_pause)

        play.visibility = View.INVISIBLE
        play.isEnabled = false
        stop.visibility = View.VISIBLE
        stop.isEnabled = true

        val intent = Intent(this, SpeechService::class.java)
        startService(intent)
    }

    override fun onDestroy() {
        val intent = Intent(this, SpeechService::class.java)
        stopService(intent)
        super.onDestroy()
    }
}