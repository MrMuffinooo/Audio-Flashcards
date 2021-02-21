package com.muffin.audioflashcards

class FlashCard(var word: String, var translation: String) {
    companion object {
        var id: Int = 1;
    }

    var fails: Int = 0
    var successes: Int = 0
    var learned: Boolean = false // if user wants to listen to other flashcards
    val ID = id


    //TODO(implement class)
}