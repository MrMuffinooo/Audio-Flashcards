package com.muffin.audioflashcards

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson


class AddActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        val prefs = getSharedPreferences("flashcards", Context.MODE_PRIVATE)
        val prefsEditor = prefs.edit()
        val gson = Gson()
        val json: String? = prefs.getString("list", "")
        val list: FlashcardsStorage = gson.fromJson(json, FlashcardsStorage::class.java)

        var word = findViewById<EditText>(R.id.etxt_word)
        var translation = findViewById<EditText>(R.id.etxt_translation)
        val extra = findViewById<EditText>(R.id.etxt_extra)

        val imeManager = applicationContext.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager



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


            val f  = FlashCard(word.text.toString(), translation.text.toString(), extra.text.toString())

            list.addFlashcard(f)

            val json = gson.toJson(list)
            prefsEditor.putString("list", json)
            prefsEditor.commit()

            word.setText("")
            translation.setText("")
            extra.setText("")
            word.requestFocus()

        }

        translation.setOnFocusChangeListener{ v, hasfocus->
            if(hasfocus){
                translation.setSelection(translation.text.length)
            }
        }

        extra.setOnFocusChangeListener{ v, hasfocus->
            if(hasfocus){
                translation.setSelection(extra.text.length)
            }
        }

        extra.setOnEditorActionListener lambda@{ v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE){
                findViewById<ImageButton>(R.id.btn_add).performClick()
                return@lambda true
            }
            return@lambda false
        }
    }
}