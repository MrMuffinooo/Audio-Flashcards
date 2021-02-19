package com.muffin.audioflashcards

class FlashCard(var word: String, var translation: String) {
    var fails: Int = 0
    var learned: Boolean = false // if user wants to listen to other flashcards

    companion object {
        var id: Int = 1;
    }

    //TODO(implement class)
}