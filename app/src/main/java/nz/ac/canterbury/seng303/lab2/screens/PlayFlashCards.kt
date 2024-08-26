package nz.ac.canterbury.seng303.lab2.screens

import android.app.AlertDialog
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import nz.ac.canterbury.seng303.lab2.viewmodels.FlashcardViewModel

@Composable
fun PlayFlashcardsScreen(
    navController: NavController,
    flashcardViewModel: FlashcardViewModel
) {
    // Observe flashcards and current index
    val flashcards by flashcardViewModel.flashcards.collectAsState()
    val currentIndex by flashcardViewModel.currentIndex.collectAsState()
    val selectedAnswerIndex by flashcardViewModel.selectedAnswerIndex.collectAsState()
    val userAnswers = remember { mutableListOf<Int>() }
    val context = LocalContext.current

    // State for showing the result dialog
    var showToast by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf("") }
    var moveToNextFlashcard by remember { mutableStateOf(false) }

    // Initialize the game when the screen is first loaded
    LaunchedEffect(Unit) {
        flashcardViewModel.startFlashcardGame()
    }

    val currentFlashcard = flashcards.getOrNull(currentIndex)

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (currentFlashcard != null) {

        val nonEmptyAnswers = currentFlashcard.answers.filter { it.text.isNotBlank() }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            if (!isLandscape) {
                Text(
                    text = "Play Flashcards",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 16.dp)
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray, RoundedCornerShape(8.dp))
                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Text(
                    text = currentFlashcard.question,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                )
            }
            // Scrollable list of answers
            LazyColumn(
                modifier = Modifier
                    .weight(1f) // Take up remaining space
                    .padding(vertical = 8.dp)
            ) {
                items(nonEmptyAnswers.size) { index ->
                    val answer = nonEmptyAnswers[index]
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
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
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        if (selectedAnswerIndex != null) {
                            isCorrect = selectedAnswerIndex == currentFlashcard.correctAnswerIndex
                            toastMessage = if (isCorrect) "Correct Answer" else "Wrong Answer"
                            moveToNextFlashcard = true
                            showToast = true
                            userAnswers.add(selectedAnswerIndex ?: -1)
                        } else {
                            toastMessage = "Please select an answer"
                            showToast = true
                            moveToNextFlashcard = false
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(vertical = 20.dp) // Add padding to ensure visibility
                ) {
                    Text(text = "Submit")
                }
                Text(
                    text = "${currentIndex + 1}/${flashcards.size}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 20.sp,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }

        // Show result dialog
        if (showToast) {
            LaunchedEffect(showToast) {
                Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
                showToast = false
                if (moveToNextFlashcard) {
                    flashcardViewModel.moveToNextFlashcard()

                    if (currentIndex + 1 >= flashcards.size) {
                        flashcardViewModel.navigateToSummary(navController, userAnswers)
                    } else {
                        flashcardViewModel.clearSelection()
                    }
                }
            }
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
                text = "There are no cards created.\nPlease create some cards",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Gray
            )
        }
    }
}
