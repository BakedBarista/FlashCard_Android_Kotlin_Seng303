package nz.ac.canterbury.seng303.lab2.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import nz.ac.canterbury.seng303.lab2.viewmodels.FlashcardViewModel

@Composable
fun HighScoreScreen(viewModel: FlashcardViewModel) {
    val highScores = viewModel.getHighScores().collectAsState(initial = emptyList()).value

    LazyColumn(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize() // Fill the maximum available size
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "High Scores",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }

        items(highScores) { score ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = "${highScores.indexOf(score) + 1}.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = score.name,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(3f),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "${score.score}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End
                )
            }
            Divider()
        }
    }
}
