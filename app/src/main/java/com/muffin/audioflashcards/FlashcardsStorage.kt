package com.muffin.audioflashcards

import java.util.ArrayList

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
}


