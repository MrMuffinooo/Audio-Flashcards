package com.muffin.audioflashcards

import android.content.Context
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson


class AddActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        val prefs = getSharedPreferences("flashcards",Context.MODE_PRIVATE)
        val prefsEditor = prefs.edit()
        val gson = Gson()
        val json: String? = prefs.getString("list", "")
        val list: FlashcardsStorage = gson.fromJson(json, FlashcardsStorage::class.java)


        findViewById<ImageButton>(R.id.btn_add).setOnClickListener {
            var word = findViewById<EditText>(R.id.etxt_word)
            if (word.text.toString().isEmpty()){
                word.requestFocus()
                return@setOnClickListener
            }
            var translation = findViewById<EditText>(R.id.etxt_translation)
            if (translation.text.toString().isEmpty()){
                translation.requestFocus()
                return@setOnClickListener
            }
            val extra = findViewById<EditText>(R.id.etxt_extra)

            val f  = FlashCard(word.text.toString(),translation.text.toString(),extra.text.toString())

            list.addFlashcard(f)

            val json = gson.toJson(list)
            prefsEditor.putString("list", json)
            prefsEditor.commit()

            word.setText("")
            translation.setText("")
            extra.setText("")
            word.requestFocus()

        }
    }
}