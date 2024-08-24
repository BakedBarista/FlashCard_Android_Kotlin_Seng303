package nz.ac.canterbury.seng303.lab2.screens

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import nz.ac.canterbury.seng303.lab2.viewmodels.FlashcardViewModel
import androidx.compose.foundation.selection.toggleable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

@Composable
fun PlayFlashcardsScreen(
    navController: NavController,
    flashcardViewModel: FlashcardViewModel
) {
    // Observe flashcards and current index
    val flashcards by flashcardViewModel.flashcards.collectAsState()
    val currentIndex by flashcardViewModel.currentIndex.collectAsState()
    val selectedAnswerIndex by flashcardViewModel.selectedAnswerIndex.collectAsState()

    // State for showing the result dialog
    var showDialog by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }

    // Initialize the game when the screen is first loaded
    LaunchedEffect(Unit) {
        flashcardViewModel.startFlashcardGame()
    }

    val currentFlashcard = flashcards.getOrNull(currentIndex)

    if (currentFlashcard != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Question ${currentIndex + 1}/${flashcards.size}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = currentFlashcard.question,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            currentFlashcard.answers.forEachIndexed { index, answer ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp
                        )
                ) {
                    RadioButton(
                        selected = selectedAnswerIndex == index,
                        onClick = { flashcardViewModel.selectAnswer(index) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = answer.text,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        if (selectedAnswerIndex != null) {
                            isCorrect = selectedAnswerIndex == currentFlashcard.correctAnswerIndex
                            showDialog = true
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Submit")
                }
                Text(
                    text = "Question ${currentIndex + 1}/${flashcards.size}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
        }

        // Show result dialog
        if (showDialog) {
            AlertDialog(
                onDismissRequest = {
                    showDialog = false
                    flashcardViewModel.moveToNextFlashcard()
                },
                title = {
                    Text(text = if (isCorrect) "Correct Answer" else "Wrong Answer")
                },
                confirmButton = {
                    Button(onClick = {
                        showDialog = false
                        flashcardViewModel.moveToNextFlashcard()
                    }) {
                        Text("OK")
                    }
                }
            )
        }
    } else {
        // Handle the case where no flashcards are available
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No flashcards available.",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Gray
            )
        }
    }
}
