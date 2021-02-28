package com.muffin.audioflashcards

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity


class FlashcardEditActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flashcard_edit)
        val w = intent.extras?.getString("WORD")
        val t = intent.extras?.getString("TRANSLATION")
        val e = intent.extras?.getString("EXTRA")

        val word = findViewById<EditText>(R.id.etxt_word)
        val translation = findViewById<EditText>(R.id.etxt_translation)
        val extra = findViewById<EditText>(R.id.etxt_extra)

        word.setText(w)
        translation.setText(t)
        extra.setText(e)

        findViewById<ImageButton>(R.id.btn_add).setOnClickListener {
            if (word.text.toString().isEmpty()){
                word.requestFocus()
                return@setOnClickListener
            }
            if (translation.text.toString().isEmpty()){
                translation.requestFocus()
                return@setOnClickListener
            }
            val data = Intent()
            data.putExtra("WORD", word.text.toString())
            data.putExtra("TRANSLATION", translation.text.toString())
            data.putExtra("EXTRA", extra.text.toString())
            setResult(RESULT_OK, data)
            finish()
        }

    }
}