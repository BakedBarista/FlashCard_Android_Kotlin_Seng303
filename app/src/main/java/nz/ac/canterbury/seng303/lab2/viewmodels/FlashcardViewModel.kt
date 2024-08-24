package nz.ac.canterbury.seng303.lab2.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import nz.ac.canterbury.seng303.lab2.datastore.Storage
import nz.ac.canterbury.seng303.lab2.models.Answer
import nz.ac.canterbury.seng303.lab2.models.Flashcard
import kotlin.random.Random

class FlashcardViewModel(
    private val flashcardStorage: Storage<Flashcard>
) : ViewModel() {

    private val _flashcards = MutableStateFlow<List<Flashcard>>(emptyList())
    val flashcards: StateFlow<List<Flashcard>> get() = _flashcards

    private val _selectedFlashcard = MutableStateFlow<Flashcard?>(null)
    val selectedFlashcard: StateFlow<Flashcard?> = _selectedFlashcard

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> get() = _currentIndex

    private val _selectedAnswerIndex = MutableStateFlow<Int?>(null)
    val selectedAnswerIndex: StateFlow<Int?> get() = _selectedAnswerIndex

    fun getFlashcards() = viewModelScope.launch {
        flashcardStorage.getAll()
            .catch { Log.e("FLASHCARD_VIEW_MODEL", it.toString()) }
            .collect { _flashcards.emit(it) }
    }

    fun createFlashcard(question: String, answers: List<Answer>, correctAnswerIndex: Int) = viewModelScope.launch {
        val flashcard = Flashcard(
            id = Random.nextInt(0, Int.MAX_VALUE),
            question = question,
            answers = answers,
            correctAnswerIndex = correctAnswerIndex,
        )
        val gson = Gson()
        val jsonString = gson.toJson(flashcard)
        Log.d("FLASHCARD_VIEW_MODEL", "JSON String: $jsonString")
        Log.d("FLASHCARD_VIEW_MODEL", "Creating flashcard: $flashcard")
        flashcardStorage.insert(flashcard)
            .catch { Log.e("FLASHCARD_VIEW_MODEL", "Could not insert flashcard") }
            .collect()
        flashcardStorage.getAll()
            .catch { Log.e("FLASHCARD_VIEW_MODEL", it.toString()) }
            .collect { _flashcards.emit(it) }
    }

    fun getFlashcardById(flashcardId: Int?) = viewModelScope.launch {
        if (flashcardId != null) {
            _selectedFlashcard.value = flashcardStorage.get { it.getIdentifier() == flashcardId }.first()
        } else {
            _selectedFlashcard.value = null
        }
    }

    fun deleteFlashcardById(flashcardId: Int?) = viewModelScope.launch {
        Log.d("FLASHCARD_VIEW_MODEL", "Deleting flashcard: $flashcardId")
        if (flashcardId != null) {
            flashcardStorage.delete(flashcardId).collect()
            flashcardStorage.getAll()
                .catch { Log.e("FLASHCARD_VIEW_MODEL", it.toString()) }
                .collect { _flashcards.emit(it) }
        }
    }

    fun editFlashcardById(flashcardId: Int?, flashcard: Flashcard) = viewModelScope.launch {
        Log.d("FLASHCARD_VIEW_MODEL", "Editing flashcard: $flashcardId")
        if (flashcardId != null) {
            flashcardStorage.edit(flashcardId, flashcard).collect()
            flashcardStorage.getAll()
                .catch { Log.e("FLASHCARD_VIEW_MODEL", it.toString()) }
                .collect { _flashcards.emit(it) }
        }
    }

    // Function to start the flashcard game
    fun startFlashcardGame() {
        _currentIndex.value = 0
        _selectedAnswerIndex.value = null
    }

    // Function to move to the next flashcard
    fun moveToNextFlashcard() {
        if (_currentIndex.value < (_flashcards.value.size - 1)) {
            _currentIndex.value += 1
        } else {
            // Handle the end of the game, e.g., navigate back to the main screen or show a summary
            Log.d("FLASHCARD_VIEW_MODEL", "End of flashcards")
        }
    }

    // Function to select an answer
    fun selectAnswer(index: Int) {
        _selectedAnswerIndex.value = index
    }
}
