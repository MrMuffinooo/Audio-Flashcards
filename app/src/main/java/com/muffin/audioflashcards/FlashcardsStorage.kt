package com.muffin.audioflashcards

import java.util.*

class FlashcardsStorage {
    var list:ArrayList<FlashCard> = ArrayList()

    fun addFlashcard(f:FlashCard){
        if (f in list) return
        list.add(f)
    }

    fun removeFlashcard(f:FlashCard){
        list.remove(f)
    }

    fun getSize():Int{
        return list.size
    }

    fun getSetToListen():FlashcardsStorage{
        var r = FlashcardsStorage()
        list.shuffle()

       /* list.sortBy { it.answerBalance }
        for (f in list){
            r.addFlashcard(f)
            if (r.getSize()>=5) break
        }
        list.sortBy { it.lastListen }
        for (f in list){
            r.addFlashcard(f)
            if (r.getSize()>=10) break
        }*/
        for (f in list){
            r.addFlashcard(f)
            if (r.getSize()>=10) break
        }


        return r
    }

    fun getSetToPlay():FlashcardsStorage{
        var r = FlashcardsStorage()
        list.shuffle()

        for (f in list){
            r.addFlashcard(f)
            if (r.getSize()>=10) break
        }

        return r
    }

    fun getRandomFlashcard():FlashCard{
        return list[Random().nextInt(list.size)]
    }

    fun get(i:Int): FlashCard {
        return list[i]
    }
}


