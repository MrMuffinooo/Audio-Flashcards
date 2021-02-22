package com.muffin.audioflashcards

import java.util.*

class FlashCard(var word: String, var translation: String, var extra: String = "") {
    companion object {
        var id: Int = 1;
    }

    var answerBalance:Int = 0
    var learned: Boolean = false // if user wants to listen to other flashcards
    val ID = id
    var lastListen: Date = Date()


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FlashCard

        if (word != other.word) return false
        if (translation != other.translation) return false
        if (extra != other.extra) return false

        return true
    }

    override fun hashCode(): Int {
        var result = word.hashCode()
        result = 31 * result + translation.hashCode()
        result = 31 * result + extra.hashCode()
        return result
    }


}