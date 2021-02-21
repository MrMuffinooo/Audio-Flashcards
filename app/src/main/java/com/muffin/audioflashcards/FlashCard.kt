package com.muffin.audioflashcards

import java.util.*

class FlashCard(var word: String, var translation: String) {
    companion object {
        var id: Int = 1;
    }

    var answerBalance:Int = 0
    var learned: Boolean = false // if user wants to listen to other flashcards
    val ID = id
    var lastListen: Date = Date()

}