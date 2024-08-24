package nz.ac.canterbury.seng303.lab2.screens

import android.app.AlertDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import nz.ac.canterbury.seng303.lab2.models.Flashcard
import nz.ac.canterbury.seng303.lab2.viewmodels.EditFlashcardViewModel
import nz.ac.canterbury.seng303.lab2.viewmodels.FlashcardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditFlashcard(
    flashcardId: String,
    editFlashcardViewModel: EditFlashcardViewModel,
    flashcardViewModel: FlashcardViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val selectedFlashcardState by flashcardViewModel.selectedFlashcard.collectAsState(null)
    val flashcard: Flashcard? = selectedFlashcardState

    LaunchedEffect(flashcard) {
        if (flashcard == null) {
            flashcardViewModel.getFlashcardById(flashcardId.toIntOrNull())
        } else {
            editFlashcardViewModel.setDefaultValues(flashcard)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = editFlashcardViewModel.question,
            onValueChange = { editFlashcardViewModel.updateQuestion(it) },
            label = { Text("Question") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )


        LazyColumn(
            modifier = Modifier.weight(1f) // Take remaining space
        ) {
            itemsIndexed(editFlashcardViewModel.answers) { index, answer ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Checkbox(
                        checked = editFlashcardViewModel.correctAnswerIndex == index,
                        onCheckedChange = { isChecked ->
                            if (isChecked) {
                                editFlashcardViewModel.updateCorrectAnswerIndex(index)
                            } else if (editFlashcardViewModel.correctAnswerIndex == index) {
                                editFlashcardViewModel.updateCorrectAnswerIndex(-1) // Uncheck the current selection
                            }
                        }
                    )
                    OutlinedTextField(
                        value = answer.text,
                        onValueChange = { newText ->
                            editFlashcardViewModel.updateAnswer(index, newText)
                        },
                        label = { Text("Answer ${index + 1}") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp)
                    )
                }
            }
        }

        Button(
            onClick = { editFlashcardViewModel.addAnswer() },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 16.dp, bottom = 8.dp)
        ) {
            Text("+")
        }

        Button(
            onClick = {
                val builder = AlertDialog.Builder(context)
                // Validation checks
                when {
                    editFlashcardViewModel.question.isBlank() -> {
                        builder.setMessage("A flashcard must have a question")
                            .setCancelable(false)
                            .setPositiveButton("close") { dialog, id -> dialog.dismiss() }
                        val alert = builder.create()
                        alert.show()
                    }

                    editFlashcardViewModel.answers.count { it.text.isNotBlank() } < 2 -> {
                        builder.setMessage("A flashcard must have at least two answers")
                            .setCancelable(false)
                            .setPositiveButton("close") { dialog, id -> dialog.dismiss() }
                        val alert = builder.create()
                        alert.show()
                    }

                    editFlashcardViewModel.correctAnswerIndex < 0 -> {
                        builder.setMessage("A flashcard must have a correct answer")
                            .setCancelable(false)
                            .setPositiveButton("close") { dialog, id -> dialog.dismiss() }
                        val alert = builder.create()
                        alert.show()
                    }

                    else -> {
                        flashcardViewModel.editFlashcardById(
                            flashcardId.toIntOrNull(),
                            flashcard = Flashcard(
                                id = flashcardId.toInt(),
                                question = editFlashcardViewModel.question,
                                answers = editFlashcardViewModel.answers,
                                correctAnswerIndex = editFlashcardViewModel.correctAnswerIndex
                            )
                        )
                        builder.setMessage("Edited flashcard!")
                            .setCancelable(false)
                            .setPositiveButton("Ok") { dialog, id ->
                                navController.navigate("ViewFlashCards")
                            }
                        val alert = builder.create()
                        alert.show()
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Save and return")
        }
    }
}
