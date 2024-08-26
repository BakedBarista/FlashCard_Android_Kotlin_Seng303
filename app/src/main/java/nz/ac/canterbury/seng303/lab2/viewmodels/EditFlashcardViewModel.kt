package nz.ac.canterbury.seng303.lab2.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import nz.ac.canterbury.seng303.lab2.models.Answer
import nz.ac.canterbury.seng303.lab2.models.Flashcard

class EditFlashcardViewModel: ViewModel() {
    var question by mutableStateOf("")
        private set

    fun updateQuestion(newQuestion: String) {
        question = newQuestion
    }

    var answers = mutableStateListOf<Answer>().apply {
        repeat(4) { add(Answer("")) }
    }
        private set

    fun updateAnswer(index: Int, newAnswer: String) {
        if (index in answers.indices) {
            answers[index] = answers[index].copy(text = newAnswer)
        }
    }

    fun addAnswer() {
        answers.add(Answer("")) // Add a new empty answer
    }

    fun removeAnswer(index: Int) {
        if (index >= 0 && index < answers.size) {
            answers.removeAt(index)
            if (index == correctAnswerIndex) {
                correctAnswerIndex = -1
            } else if (index < correctAnswerIndex) {
                correctAnswerIndex -= 1
            }
        }
    }

    var correctAnswerIndex by mutableStateOf(-1)
        private set

    fun updateCorrectAnswerIndex(newIndex: Int) {
        correctAnswerIndex = newIndex

    }

    // Function to set the default values based on the selected flashcard
    fun setDefaultValues(selectedFlashcard: Flashcard?) {
        selectedFlashcard?.let {
            question = it.question
            answers.clear()
            answers.addAll(it.answers)
            correctAnswerIndex = it.correctAnswerIndex
        }
    }
}
