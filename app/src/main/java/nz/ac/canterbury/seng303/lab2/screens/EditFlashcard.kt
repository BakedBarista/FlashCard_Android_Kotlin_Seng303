package nz.ac.canterbury.seng303.lab2.screens

import android.app.AlertDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
        Text(
            text = "Edit flash card",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = editFlashcardViewModel.question,
            onValueChange = { editFlashcardViewModel.updateQuestion(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .background(Color.LightGray, RoundedCornerShape(8.dp)),
        )


        LazyColumn(
            modifier = Modifier.weight(1f)
                .padding(bottom = 16.dp)
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
                                if (answer.text.isNotBlank()) {
                                    editFlashcardViewModel.updateCorrectAnswerIndex(index)
                                }
                            } else if (editFlashcardViewModel.correctAnswerIndex == index) {
                                editFlashcardViewModel.updateCorrectAnswerIndex(-1) // Uncheck the current selection
                            }
                        }
                    )
                    OutlinedTextField(
                        value = answer.text,
                        onValueChange = { newText ->
                            editFlashcardViewModel.updateAnswer(index, newText)
                            if (index == editFlashcardViewModel.correctAnswerIndex && newText.isBlank()) {
                                editFlashcardViewModel.updateCorrectAnswerIndex(-1)
                            }
                        },
                        modifier = Modifier
                            .weight(8f)
                            .padding(start = 8.dp)
                            .background(Color.LightGray, RoundedCornerShape(8.dp))
                    )
                    IconButton(
                        onClick = { editFlashcardViewModel.removeAnswer(index) },
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Answer",
                            tint = Color.Red
                        )
                    }
                }
            }
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = { editFlashcardViewModel.addAnswer() },
                        modifier = Modifier
                            .height(40.dp)
                            .width(100.dp)
                    ) {
                        Text(text = "+")
                    }
                }
            }
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

                        navController.navigate("ViewFlashCards")

                    }
                }
            },
            modifier = Modifier
                .padding(bottom = 8.dp)
                .heightIn(min = 48.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Save and return")
        }
    }
}
