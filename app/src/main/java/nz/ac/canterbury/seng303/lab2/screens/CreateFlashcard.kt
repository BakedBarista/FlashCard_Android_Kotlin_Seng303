package nz.ac.canterbury.seng303.lab2.screens

import android.app.AlertDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
        Text(
            text = "Add a New Flashcard",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = question,
            onValueChange = { newQuestion -> onQuestionChange(newQuestion) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .background(Color.LightGray, RoundedCornerShape(8.dp)),
            placeholder = {
                Text(
                    text = "Input question here"
                )
            }
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 16.dp)
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
                                if (answer.text.isNotBlank()) {
                                    onCorrectAnswerChange(index)
                                }
                            } else if (correctAnswerIndex == index) {
                                onCorrectAnswerChange(-1)
                            }
                        }
                    )
                    OutlinedTextField(
                        value = answer.text,
                        onValueChange = { newText ->
                            onAnswerChange(index, newText)
                            if (index == correctAnswerIndex && newText.isBlank()) {
                                onCorrectAnswerChange(-1)
                            }
                        },
                        modifier = Modifier
                            .weight(8f)
                            .padding(start = 8.dp)
                            .background(Color.LightGray, RoundedCornerShape(8.dp))
                    )
                    IconButton(
                        onClick = { viewModel.removeAnswer(index) },
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
                        onClick = { viewModel.addAnswer() },
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
                    question.isBlank() -> {
                        builder.setMessage("A flashcard must have a question")
                            .setCancelable(false)
                            .setPositiveButton("Close") { dialog, _ -> dialog.dismiss() }
                        val alert = builder.create()
                        alert.show()
                    }
                    answers.count { it.text.isNotBlank() } < 2 -> {
                        builder.setMessage("A flashcard must have at least two answers")
                            .setCancelable(false)
                            .setPositiveButton("Close") { dialog, _ -> dialog.dismiss() }
                        val alert = builder.create()
                        alert.show()
                    }
                    correctAnswerIndex < 0 -> {
                        builder.setMessage("A flashcard must have a correct answer")
                            .setCancelable(false)
                            .setPositiveButton("Close") { dialog, _ -> dialog.dismiss() }
                        val alert = builder.create()
                        alert.show()
                    }
                    else -> {
                        createFlashcardFn(question, answers, correctAnswerIndex)
                        onQuestionChange("")
                        answers.forEachIndexed { index, _ ->
                            onAnswerChange(index, "")
                        }
                        onCorrectAnswerChange(-1)
                        navController.navigate("Home")
                    }
                }
            },
            modifier = Modifier
                .padding(bottom = 8.dp)
                .heightIn(min = 48.dp) // Ensure button is not too small
                .align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Save and return")
        }
    }
}
