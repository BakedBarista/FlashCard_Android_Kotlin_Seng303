package nz.ac.canterbury.seng303.lab2.viewmodels


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import nz.ac.canterbury.seng303.lab2.models.Answer


class CreateFlashcardViewModel : ViewModel() {
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

    var correctAnswerIndex by mutableStateOf(-1)
        private set

    fun updateCorrectAnswerIndex(newIndex: Int) {
        correctAnswerIndex = newIndex
    }

    fun reset() {
        question = ""
        answers.clear()
        repeat(4) { answers.add(Answer("")) }
        correctAnswerIndex = -1
    }
}