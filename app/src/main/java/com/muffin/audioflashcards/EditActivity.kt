package com.muffin.audioflashcards

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson


class EditActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        val prefs = getSharedPreferences("flashcards", Context.MODE_PRIVATE)
        val prefsEditor = prefs.edit()
        val gson = Gson()
        val json: String? = prefs.getString("list", "")
        val list: FlashcardsStorage = gson.fromJson(json, FlashcardsStorage::class.java)


        val recycler = findViewById<RecyclerView>(R.id.Recycler)
        recycler.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        val adapter = RecyclerAdapter(list)

        recycler.layoutManager = layoutManager
        recycler.adapter = adapter
    }
}