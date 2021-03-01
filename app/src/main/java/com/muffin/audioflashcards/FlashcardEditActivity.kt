package com.muffin.audioflashcards

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
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

        word.requestFocus()
        val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)



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

        extra.setOnEditorActionListener lambda@{ v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE){
                findViewById<ImageButton>(R.id.btn_add).performClick()
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                return@lambda true
            }
            return@lambda false
        }

        translation.setOnFocusChangeListener{v,hasfocus->
            if(hasfocus){
                translation.setSelection(translation.text.length)
            }
        }

        extra.setOnFocusChangeListener{v,hasfocus->
            if(hasfocus){
                translation.setSelection(extra.text.length)
            }
        }


    }
}