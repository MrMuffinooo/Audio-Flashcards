package com.muffin.audioflashcards

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson


class EditActivity : AppCompatActivity() {
    lateinit var adapter: RecyclerAdapter
    var pos = -1
    lateinit var list: FlashcardsStorage
    lateinit var gson: Gson
    lateinit var prefsEditor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        val prefs = getSharedPreferences("flashcards", Context.MODE_PRIVATE)
        prefsEditor = prefs.edit()
        gson = Gson()
        val json: String? = prefs.getString("list", "")
        list = gson.fromJson(json, FlashcardsStorage::class.java)

        val recycler = findViewById<RecyclerView>(R.id.Recycler)
        recycler.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        adapter = RecyclerAdapter(list)

        recycler.layoutManager = layoutManager
        recycler.adapter = adapter


        val obj = object : RecyclerAdapter.OnItemClickListener {  // <- Weird Kotlin thing
            override fun onItemClick(position: Int) {

                val intent = Intent(baseContext, FlashcardEditActivity::class.java)//????????
                pos = position
                intent.putExtra("WORD", list.get(position).word)
                intent.putExtra("TRANSLATION", list.get(position).translation)
                intent.putExtra("EXTRA", list.get(position).extra)
                startActivityForResult(intent, position)
                //list.get(pos).word = "XxXxX"
                //adapter.notifyItemChanged(pos)

            }

            override fun onDeleteClick(position: Int) {
                list.removeAt(position)
                val json = gson.toJson(list)
                prefsEditor.putString("list", json)
                prefsEditor.commit()
                adapter.notifyItemRemoved(position)
            }
        }
        adapter.setOnItemClickListener(obj)

    }

     override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
         if (resultCode== RESULT_OK && pos != -1 && data != null){
             list.get(pos).word = data.getStringExtra("WORD").toString()
             list.get(pos).translation = data.getStringExtra("TRANSLATION").toString()
             list.get(pos).extra = data.getStringExtra("EXTRA").toString()
             adapter.notifyItemChanged(pos)
             val json = gson.toJson(list)
             prefsEditor.putString("list", json)
             prefsEditor.commit()
         }

    }

}