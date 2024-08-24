package nz.ac.canterbury.seng303.lab2.screens

import android.app.AlertDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import nz.ac.canterbury.seng303.lab2.models.Answer
import nz.ac.canterbury.seng303.lab2.viewmodels.CreateFlashcardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateFlashcard(
    navController: NavController,
    question: String,
    onQuestionChange: (String) -> Unit,
    answers: List<Answer>,
    onAnswerChange: (Int, String) -> Unit,
    correctAnswerIndex: Int,
    onCorrectAnswerChange: (Int) -> Unit,
    createFlashcardFn: (String, List<Answer>, Int) -> Unit,
    viewModel: CreateFlashcardViewModel
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = question,
            onValueChange = { newQuestion -> onQuestionChange(newQuestion) },
            label = { Text("Question") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f) // Take remaining space
        ) {
            itemsIndexed(answers) { index, answer ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Checkbox(
                        checked = correctAnswerIndex == index,
                        onCheckedChange = { isChecked ->
                            if (isChecked) {
                                onCorrectAnswerChange(index)
                            } else if (correctAnswerIndex == index) {
                                onCorrectAnswerChange(-1)
                            }
                        }
                    )
                    OutlinedTextField(
                        value = answer.text,
                        onValueChange = { newText ->
                            onAnswerChange(index, newText)
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
            onClick = { viewModel.addAnswer() },
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
                    question.isBlank() -> {
                        builder.setMessage("A flashcard must have a question")
                            .setCancelable(false)
                            .setPositiveButton("close") { dialog, id -> dialog.dismiss() }
                        val alert = builder.create()
                        alert.show()
                    }
                    answers.count { it.text.isNotBlank() } < 2 -> {
                        builder.setMessage("A flashcard must have at least two answers")
                            .setCancelable(false)
                            .setPositiveButton("close") { dialog, id -> dialog.dismiss() }
                        val alert = builder.create()
                        alert.show()
                    }
                    correctAnswerIndex < 0 -> {
                        builder.setMessage("A flashcard must have a correct answer")
                            .setCancelable(false)
                            .setPositiveButton("close") { dialog, id -> dialog.dismiss() }
                        val alert = builder.create()
                        alert.show()
                    }
                    else -> {
                        createFlashcardFn(question, answers, correctAnswerIndex)
                        builder.setMessage("Created flashcard!")
                            .setCancelable(false)
                            .setPositiveButton("Ok") { dialog, id ->
                                onQuestionChange("")
                                answers.indices.forEach { index ->
                                    onAnswerChange(index, "")
                                }
                                onCorrectAnswerChange(-1)
                                navController.navigate("ViewFlashCards")
                            }
                            .setNegativeButton("Cancel") { dialog, id -> dialog.dismiss() }
                        val alert = builder.create()
                        alert.show()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp) // Padding at the bottom of the screen
        ) {
            Text(text = "Save")
        }
    }

}
