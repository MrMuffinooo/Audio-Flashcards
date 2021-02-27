package com.muffin.audioflashcards

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson


class FlashcardEditActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flashcard_edit)
        val pos = intent.extras?.getInt("POSITION")

        val prefs = getSharedPreferences("flashcards", Context.MODE_PRIVATE)
        val prefsEditor = prefs.edit()
        val gson = Gson()
        val json: String? = prefs.getString("list", "")
        val list: FlashcardsStorage = gson.fromJson(json, FlashcardsStorage::class.java)

        val f : FlashCard = list.get(pos)

        val word = findViewById<EditText>(R.id.etxt_word)
        val translation = findViewById<EditText>(R.id.etxt_translation)
        val extra = findViewById<EditText>(R.id.etxt_extra)

        word.setText(f.word)
        translation.setText(f.translation)
        extra.setText(f.extra)

        findViewById<ImageButton>(R.id.btn_add).setOnClickListener {
            if (word.text.toString().isEmpty()){
                word.requestFocus()
                return@setOnClickListener
            }
            if (translation.text.toString().isEmpty()){
                translation.requestFocus()
                return@setOnClickListener
            }
            f.word = word.text.toString()
            f.translation = translation.text.toString()
            f.extra = extra.text.toString()

            val json = gson.toJson(list)
            prefsEditor.putString("list", json)
            prefsEditor.commit()


            finish()
        }

    }
}