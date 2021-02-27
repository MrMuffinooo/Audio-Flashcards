package com.muffin.audioflashcards

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson


class EditActivity : AppCompatActivity() {
    lateinit var adapter: RecyclerAdapter
    var pos = -1
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
        adapter = RecyclerAdapter(list)

        recycler.layoutManager = layoutManager
        recycler.adapter = adapter


        val obj = object : RecyclerAdapter.OnItemClickListener {  // <- Weird Kotlin thing
            override fun onItemClick(position: Int) {

                val intent = Intent(baseContext, FlashcardEditActivity::class.java)//????????
                pos = position
                intent.putExtra("POSITION", position)
                startActivityForResult(intent, position)
                //list.get(pos).word = "XxXxX"
                //adapter.notifyItemChanged(pos)

            }
        }
        adapter.setOnItemClickListener(obj)

    }

     override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, null)
        adapter.notifyItemChanged(pos)
    }

    override fun onStart() {
        super.onStart()
        if(pos != -1)
            Handler().postDelayed(this::updateAdapter, 2000)
    }

    fun updateAdapter(){
        adapter.notifyItemChanged(pos)
    }
}