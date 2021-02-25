package com.muffin.audioflashcards

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecyclerAdapter(list: FlashcardsStorage) : RecyclerView.Adapter<RecyclerAdapter.ExampleViewHolder>() {

    var storage:FlashcardsStorage = list

    class ExampleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textMain: TextView
        var textSub: TextView
        init {
            textMain = itemView.findViewById(R.id.txt_main)
            textSub = itemView.findViewById(R.id.txt_sub)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExampleViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item, parent, false)
        val evh = ExampleViewHolder(v)
        return evh
    }

    override fun onBindViewHolder(holder: ExampleViewHolder, position: Int) {
        var item = storage.get(position)

        holder.textMain.setText(item.word)
        holder.textSub.setText(item.translation)
    }

    override fun getItemCount(): Int {
        return storage.getSize()
    }
}