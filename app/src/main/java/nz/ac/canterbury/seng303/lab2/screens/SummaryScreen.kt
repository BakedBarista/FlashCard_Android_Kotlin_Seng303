package nz.ac.canterbury.seng303.lab2.screens

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import nz.ac.canterbury.seng303.lab2.models.Flashcard
import nz.ac.canterbury.seng303.lab2.viewmodels.FlashcardViewModel

@Composable
fun SummaryScreen(
    correctAnswersCount: Int,
    totalQuestions: Int,
    flashcards: List<Flashcard>,
    userAnswers: List<Int>,
    navController: NavController
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Summary",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            style = androidx.compose.material3.MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(16.dp))
        if (!isLandscape) {
            Text(
                text = "Score: $correctAnswersCount/$totalQuestions",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = androidx.compose.material3.MaterialTheme.typography.headlineLarge
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f) // Take up remaining space
        ) {
            itemsIndexed(flashcards) { index, flashcard ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .background(Color.LightGray, RoundedCornerShape(8.dp))
                ) {
                    Text(
                        text = flashcard.question,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 32.dp, top = 8.dp, bottom = 8.dp) // Padding for the icon
                    )
                    Icon(
                        imageVector = if (userAnswers.getOrNull(index) == flashcard.correctAnswerIndex) {
                            Icons.Default.Check
                        } else {
                            Icons.Default.Close
                        },
                        contentDescription = null,
                        tint = if (userAnswers.getOrNull(index) == flashcard.correctAnswerIndex) {
                            Color.Green
                        } else {
                            Color.Red
                        },
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(start = 8.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Button to navigate to the High Score screen
        Button(
            onClick = {
                navController.navigate("highscores") // Replace with your actual route to the high score screen
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "View High Scores")
        }
    }
}
