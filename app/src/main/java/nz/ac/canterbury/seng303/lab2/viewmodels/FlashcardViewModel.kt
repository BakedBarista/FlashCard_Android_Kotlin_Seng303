package nz.ac.canterbury.seng303.lab2.viewmodels

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import nz.ac.canterbury.seng303.lab2.datastore.Storage
import nz.ac.canterbury.seng303.lab2.models.Answer
import nz.ac.canterbury.seng303.lab2.models.Flashcard
import nz.ac.canterbury.seng303.lab2.models.HighScore
import kotlin.random.Random

class FlashcardViewModel(
    private val flashcardStorage: Storage<Flashcard>,
    private val context: Context
) : ViewModel() {

    private val _flashcards = MutableStateFlow<List<Flashcard>>(emptyList())
    val flashcards: StateFlow<List<Flashcard>> get() = _flashcards

    private val _selectedFlashcard = MutableStateFlow<Flashcard?>(null)
    val selectedFlashcard: StateFlow<Flashcard?> = _selectedFlashcard

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> get() = _currentIndex

    private val _selectedAnswerIndex = MutableStateFlow<Int?>(null)
    val selectedAnswerIndex: StateFlow<Int?> get() = _selectedAnswerIndex

    private val _correctAnswersCount = MutableStateFlow(0)
    val correctAnswersCount: StateFlow<Int> get() = _correctAnswersCount

    private val _playerName = MutableStateFlow<String?>(null)
    val playerName: StateFlow<String?> get() = _playerName

    private val _highScores = MutableStateFlow<List<HighScore>>(emptyList())
    val highScores: StateFlow<List<HighScore>> get() = _highScores

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("FlashcardApp", Context.MODE_PRIVATE)

    init {
        getFlashcards()
        loadHighScores()
    }

    fun setPlayerName(name: String) {
        _playerName.value = name
    }

    fun saveHighScore() {
        val name = _playerName.value ?: return
        val score = _correctAnswersCount.value

        val newHighScore = HighScore(name, score)

        val updatedScores = _highScores.value.toMutableList()
        updatedScores.add(newHighScore)
        _highScores.value = updatedScores.sortedByDescending { it.score }

        saveHighScoresToStorage(_highScores.value)
    }

    private fun loadHighScores() {
        val json = sharedPreferences.getString("high_scores", null)
        if (json != null) {
            val type = object : TypeToken<List<HighScore>>() {}.type
            _highScores.value = Gson().fromJson(json, type)
        }
    }

    private fun saveHighScoresToStorage(highScores: List<HighScore>) {
        val editor = sharedPreferences.edit()
        val json = Gson().toJson(highScores)
        editor.putString("high_scores", json)
        editor.apply()
    }

    fun getHighScores(): Flow<List<HighScore>> {
        return _highScores
    }
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
        _flashcards.value = _flashcards.value.map { flashcard ->
            val shuffledAnswers = flashcard.answers.shuffled()
            val newCorrectAnswerIndex = shuffledAnswers.indexOfFirst { it.text == flashcard.answers[flashcard.correctAnswerIndex].text }
            flashcard.copy(
                answers = shuffledAnswers,
                correctAnswerIndex = newCorrectAnswerIndex
            )
        }.shuffled()
        _currentIndex.value = 0
        _correctAnswersCount.value = 0
        _selectedAnswerIndex.value = null
    }

    // Function to move to the next flashcard
    fun moveToNextFlashcard() {
        if (_selectedAnswerIndex.value != null) {
            val currentFlashcard = _flashcards.value[_currentIndex.value]
            if (_selectedAnswerIndex.value == currentFlashcard.correctAnswerIndex) {
                _correctAnswersCount.value += 1
            }
        }

        if (_currentIndex.value < (_flashcards.value.size - 1)) {
            _currentIndex.value += 1
        } else {
            Log.d("FLASHCARD_VIEW_MODEL", "End of flashcards")
        }
    }

    // Function to select an answer
    fun selectAnswer(index: Int) {
        _selectedAnswerIndex.value = index
    }

    fun clearSelection() {
        _selectedAnswerIndex.value = null
    }

    fun calculateCorrectAnswers(userAnswers: List<Int>): Int {
        var count = 0
        for ((index, answerIndex) in userAnswers.withIndex()) {
            val flashcard = _flashcards.value.getOrNull(index) ?: continue
            if (answerIndex == flashcard.correctAnswerIndex) {
                count++
            }
        }
        return count
    }

    fun navigateToSummary(navController: NavController, userAnswers: List<Int>) {
        val updatedCorrectAnswersCount = calculateCorrectAnswers(userAnswers)
        navController.navigate(
            "summary/${updatedCorrectAnswersCount}/${_flashcards.value.size}/${userAnswers.joinToString(",")}"
        )
    }


}
